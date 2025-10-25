#!/usr/bin/env bash
set -euo pipefail

: "${BASE_URL:=http://localhost:8080}"
: "${SUFFIX:=$(date +%s)}"

if ! command -v jq >/dev/null 2>&1; then
  echo "Script ini membutuhkan jq untuk mem-parsing JSON" >&2
  exit 1
fi

ADMIN_USERNAME="admin"
ADMIN_PASSWORD="AdminPass123!"

ALUMNI_USERNAME="alumni_${SUFFIX}"
ALUMNI_EMAIL="alumni_${SUFFIX}@example.com"
ALUMNI_PASSWORD="Rahasia123!"

ADMIN_USERNAME="${ADMIN_USERNAME:-$ALUMNI_USERNAME}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-$ALUMNI_PASSWORD}"

TOTAL_STEPS=12
INSTITUTION_CREATED=false
CREATED_INSTITUTION_ID=""
WORK_HISTORY_ID=""
ADMIN_TOKEN=""

echo "==> [1/${TOTAL_STEPS}] Registrasi alumni baru"
REGISTER_PAYLOAD=$(jq -n \
  --arg username "$ALUMNI_USERNAME" \
  --arg email "$ALUMNI_EMAIL" \
  --arg password "$ALUMNI_PASSWORD" \
  --arg confirmPassword "$ALUMNI_PASSWORD" \
  --arg fullName "Alumni Demo $SUFFIX" \
  --arg nim "11190$SUFFIX" \
  --argjson angkatan 63 \
  --arg programStudi "D4" \
  --arg jurusan "D4 Komputasi Statistik" \
  --argjson tahunLulus 2024 \
  --arg phoneNumber "0812000$SUFFIX" \
  --arg alamat "Jl. Merdeka No.$SUFFIX, Jakarta" \
  '{
    username: $username,
    email: $email,
    password: $password,
    confirmPassword: $confirmPassword,
    fullName: $fullName,
    nim: $nim,
    angkatan: $angkatan,
    programStudi: $programStudi,
    jurusan: $jurusan,
    tahunLulus: $tahunLulus,
    phoneNumber: $phoneNumber,
    alamat: $alamat
  }')
REGISTER_RESPONSE=$(curl -sS -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  --data "$REGISTER_PAYLOAD")
echo "$REGISTER_RESPONSE" | jq

echo "==> [2/${TOTAL_STEPS}] Login alumni dan simpan akses token"
LOGIN_PAYLOAD=$(jq -n \
  --arg username "$ALUMNI_USERNAME" \
  --arg password "$ALUMNI_PASSWORD" \
  '{username: $username, password: $password}')
TOKEN=$(curl -sS -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  --data "$LOGIN_PAYLOAD" | jq -r '.data.accessToken // empty')
if [ -z "$TOKEN" ]; then
  echo "Gagal mengambil token login alumni" >&2
  exit 1
fi
AUTH_HEADER="Authorization: Bearer $TOKEN"
echo "Bearer token alumni disimpan."

if [ "$ADMIN_USERNAME" = "$ALUMNI_USERNAME" ] && [ "$ADMIN_PASSWORD" = "$ALUMNI_PASSWORD" ]; then
  echo "==> [3/${TOTAL_STEPS}] Menggunakan token alumni untuk operasi institusi (pastikan perannya ADMIN)"
  ADMIN_TOKEN="$TOKEN"
else
  echo "==> [3/${TOTAL_STEPS}] Login admin untuk operasi institusi"
  ADMIN_LOGIN_PAYLOAD=$(jq -n \
    --arg username "$ADMIN_USERNAME" \
    --arg password "$ADMIN_PASSWORD" \
    '{username: $username, password: $password}')
  ADMIN_TOKEN=$(curl -sS -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    --data "$ADMIN_LOGIN_PAYLOAD" | jq -r '.data.accessToken // empty')
  if [ -z "$ADMIN_TOKEN" ]; then
    echo "Gagal mengambil token admin untuk operasi institusi" >&2
    exit 1
  fi
fi
ADMIN_AUTH_HEADER="Authorization: Bearer $ADMIN_TOKEN"

echo "==> [4/${TOTAL_STEPS}] Ambil profil pengguna"
curl -sS -X GET "$BASE_URL/api/users/profile" \
  -H "$AUTH_HEADER" | jq

echo "==> [5/${TOTAL_STEPS}] Perbarui profil dengan data terbaru"
UPDATE_PROFILE_PAYLOAD=$(jq -n \
  --arg fullName "Alumni Demo ${SUFFIX} (Updated)" \
  --arg programStudi "D4" \
  --arg jurusan "D4 Komputasi Statistik" \
  --argjson tahunLulus 2024 \
  --arg phoneNumber "0812999$SUFFIX" \
  --arg alamat "Jl. Alumni No.$SUFFIX, Depok" \
  '{
    fullName: $fullName,
    programStudi: $programStudi,
    jurusan: $jurusan,
    tahunLulus: $tahunLulus,
    phoneNumber: $phoneNumber,
    alamat: $alamat
  }')
curl -sS -X PUT "$BASE_URL/api/users/profile" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  --data "$UPDATE_PROFILE_PAYLOAD" | jq

echo "==> [6/${TOTAL_STEPS}] Pastikan ada institusi untuk relasi riwayat kerja"
INSTITUTIONS_JSON=$(curl -sS -X GET "$BASE_URL/api/institutions?page=0&size=5" \
  -H "$AUTH_HEADER")
echo "$INSTITUTIONS_JSON" | jq '.data.content | map({id,name,type,province,city})'
INSTITUTION_ID=$(echo "$INSTITUTIONS_JSON" | jq -r '.data.content[0].id // empty')

if [ -z "$INSTITUTION_ID" ]; then
  echo "Daftar institusi kosong; membuat institusi demo baru"
  INSTITUTION_PAYLOAD=$(jq -n \
    --arg name "Institusi Demo $SUFFIX" \
    --arg type "SWASTA" \
    --arg province "DKI Jakarta" \
    --arg city "Jakarta Pusat" \
    --arg address "Jl. Kantor No.$SUFFIX, Jakarta" \
    --arg description "Institusi sementara untuk uji coba skenario" \
    '{
      name: $name,
      type: $type,
      province: $province,
      city: $city,
      address: $address,
      description: $description
    }')
  CREATE_INSTITUTION_RESPONSE=$(curl -sS -X POST "$BASE_URL/api/institutions" \
    -H "$ADMIN_AUTH_HEADER" \
    -H "Content-Type: application/json" \
    --data "$INSTITUTION_PAYLOAD")
  echo "$CREATE_INSTITUTION_RESPONSE" | jq
  INSTITUTION_ID=$(echo "$CREATE_INSTITUTION_RESPONSE" | jq -r '.data.id // empty')
  if [ -z "$INSTITUTION_ID" ]; then
    echo "Gagal mendapatkan id institusi baru" >&2
    exit 1
  fi
  INSTITUTION_CREATED=true
  CREATED_INSTITUTION_ID="$INSTITUTION_ID"
  echo "Institusi demo dibuat dengan id=$INSTITUTION_ID"
else
  echo "Menggunakan institutionId=$INSTITUTION_ID dari data yang sudah ada"
fi

echo "==> [7/${TOTAL_STEPS}] Tambahkan riwayat pekerjaan baru"
WORK_HISTORY_PAYLOAD=$(jq -n \
  --argjson institutionId "$INSTITUTION_ID" \
  --arg position "Data Analyst" \
  --arg employmentType "SWASTA" \
  --arg startDate "2022-08-01" \
  --arg endDate "2023-12-31" \
  --arg description "Menganalisis data statistik dan membuat dashboard." \
  '{institutionId: $institutionId,
    position: $position,
    employmentType: $employmentType,
    startDate: $startDate,
    endDate: $endDate,
    description: $description,
    isCurrentJob: false}')
WORK_HISTORY_RESPONSE=$(curl -sS -X POST "$BASE_URL/api/work-histories" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  --data "$WORK_HISTORY_PAYLOAD")
echo "$WORK_HISTORY_RESPONSE" | jq
WORK_HISTORY_ID=$(echo "$WORK_HISTORY_RESPONSE" | jq -r '.data.id // empty')
if [ -z "$WORK_HISTORY_ID" ]; then
  echo "Gagal mendapatkan id riwayat pekerjaan" >&2
  exit 1
fi

echo "==> [8/${TOTAL_STEPS}] Tandai pekerjaan sebagai aktif dengan promosi"
WORK_HISTORY_UPDATE_PAYLOAD=$(jq -n \
  --argjson institutionId "$INSTITUTION_ID" \
  --arg position "Lead Data Analyst" \
  --arg employmentType "SWASTA" \
  --arg startDate "2022-08-01" \
  --arg description "Dipromosikan menjadi lead dan memimpin tim analisis." \
  '{institutionId: $institutionId,
    position: $position,
    employmentType: $employmentType,
    startDate: $startDate,
    description: $description,
    isCurrentJob: true}')
curl -sS -X PUT "$BASE_URL/api/work-histories/$WORK_HISTORY_ID" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  --data "$WORK_HISTORY_UPDATE_PAYLOAD" | jq

echo "==> [9/${TOTAL_STEPS}] Tinjau riwayat pekerjaan & statistik dasbor"
curl -sS -X GET "$BASE_URL/api/work-histories" \
  -H "$AUTH_HEADER" | jq '.data'
curl -sS -X GET "$BASE_URL/api/statistics/overview" \
  -H "$AUTH_HEADER" | jq

echo "==> [10/${TOTAL_STEPS}] Hapus riwayat pekerjaan uji"
curl -sS -X DELETE "$BASE_URL/api/work-histories/$WORK_HISTORY_ID" \
  -H "$AUTH_HEADER" | jq

if [ "$INSTITUTION_CREATED" = true ]; then
  echo "==> [11/${TOTAL_STEPS}] Hapus institusi demo yang tadi dibuat"
  curl -sS -X DELETE "$BASE_URL/api/institutions/$CREATED_INSTITUTION_ID" \
    -H "$ADMIN_AUTH_HEADER" | jq
else
  echo "==> [11/${TOTAL_STEPS}] Melewati penghapusan institusi karena menggunakan data yang sudah ada"
fi

echo "==> [12/${TOTAL_STEPS}] Hapus akun uji (status menjadi INACTIVE)"
ACCOUNT_DELETE_PAYLOAD=$(jq -n \
  --arg password "$ALUMNI_PASSWORD" \
  --arg confirmation "DELETE" \
  '{password: $password, confirmation: $confirmation}')
curl -sS -X DELETE "$BASE_URL/api/users/account" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  --data "$ACCOUNT_DELETE_PAYLOAD" | jq

echo "Skenario selesai. Token alumni tidak berlaku lagi setelah akun dinonaktifkan."
