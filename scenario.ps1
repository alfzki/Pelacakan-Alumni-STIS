#requires -Version 7

param(
    [string]$BaseUrl = $env:BASE_URL,
    [string]$AdminUsername = $env:ADMIN_USERNAME,
    [string]$AdminPassword = $env:ADMIN_PASSWORD
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'

function ConvertTo-JsonString {
    param(
        [Parameter(ValueFromPipeline = $true)]
        $InputObject,
        [int]$Depth = 10
    )
    process {
        if ($null -eq $InputObject) {
            return 'null'
        }
        return ($InputObject | ConvertTo-Json -Depth $Depth)
    }
}

function Invoke-ApiRequest {
    param(
        [Parameter(Mandatory = $true)][ValidateSet('GET', 'POST', 'PUT', 'DELETE')]
        [string]$Method,
        [Parameter(Mandatory = $true)]
        [string]$Uri,
        [hashtable]$Headers,
        [string]$Body
    )

    $params = @{
        Method     = $Method
        Uri        = $Uri
        Headers    = $Headers
        ErrorAction = 'Stop'
    }

    if ($null -ne $Body) {
        $params['Body'] = $Body
        $params['ContentType'] = 'application/json'
    }

    return Invoke-RestMethod @params
}

if (-not $BaseUrl) {
    $BaseUrl = 'http://localhost:8080'
}

$suffix = if ($env:SUFFIX) { $env:SUFFIX } else { [DateTimeOffset]::UtcNow.ToUnixTimeSeconds().ToString() }

$alumniUsername = "alumni_$suffix"
$alumniEmail = "alumni_${suffix}@example.com"
$alumniPassword = 'Rahasia123!'

if (-not $AdminUsername) {
    $AdminUsername = 'admin'
}
if (-not $AdminPassword) {
    $AdminPassword = 'AdminPass123!'
}

if ([string]::IsNullOrWhiteSpace($AdminUsername)) {
    $AdminUsername = $alumniUsername
    $AdminPassword = $alumniPassword
}

$totalSteps = 12
$institutionCreated = $false
$createdInstitutionId = $null
$workHistoryId = $null
$adminToken = $null

Write-Host "==> [1/$totalSteps] Registrasi alumni baru"
$registerPayload = @{
    username        = $alumniUsername
    email           = $alumniEmail
    password        = $alumniPassword
    confirmPassword = $alumniPassword
    fullName        = "Alumni Demo $suffix"
    nim             = "11190$suffix"
    angkatan        = 63
    programStudi    = 'D4'
    jurusan         = 'D4 Komputasi Statistik'
    tahunLulus      = 2024
    phoneNumber     = "0812000$suffix"
    alamat          = "Jl. Merdeka No.$suffix, Jakarta"
} | ConvertTo-Json -Depth 5

$registerResponse = Invoke-ApiRequest -Method POST -Uri "$BaseUrl/api/auth/register" -Body $registerPayload
Write-Output ($registerResponse | ConvertTo-JsonString)

Write-Host "==> [2/$totalSteps] Login alumni dan simpan akses token"
$loginPayload = @{
    username = $alumniUsername
    password = $alumniPassword
} | ConvertTo-Json

$tokenResponse = Invoke-ApiRequest -Method POST -Uri "$BaseUrl/api/auth/login" -Body $loginPayload
$token = $tokenResponse.data.accessToken

if ([string]::IsNullOrWhiteSpace($token)) {
    throw "Gagal mengambil token login alumni"
}

$authHeader = @{ Authorization = "Bearer $token" }
Write-Host 'Bearer token alumni disimpan.'

if ($AdminUsername -eq $alumniUsername -and $AdminPassword -eq $alumniPassword) {
    Write-Host "==> [3/$totalSteps] Menggunakan token alumni untuk operasi institusi (pastikan perannya ADMIN)"
    $adminToken = $token
} else {
    Write-Host "==> [3/$totalSteps] Login admin untuk operasi institusi"
    $adminLoginPayload = @{
        username = $AdminUsername
        password = $AdminPassword
    } | ConvertTo-Json

    $adminLoginResponse = Invoke-ApiRequest -Method POST -Uri "$BaseUrl/api/auth/login" -Body $adminLoginPayload
    $adminToken = $adminLoginResponse.data.accessToken

    if ([string]::IsNullOrWhiteSpace($adminToken)) {
        throw "Gagal mengambil token admin untuk operasi institusi"
    }
}

$adminAuthHeader = @{ Authorization = "Bearer $adminToken" }

Write-Host "==> [4/$totalSteps] Ambil profil pengguna"
$profileResponse = Invoke-ApiRequest -Method GET -Uri "$BaseUrl/api/users/profile" -Headers $authHeader
Write-Output ($profileResponse | ConvertTo-JsonString)

Write-Host "==> [5/$totalSteps] Perbarui profil dengan data terbaru"
$updateProfilePayload = @{
    fullName     = "Alumni Demo $suffix (Updated)"
    programStudi = 'D4'
    jurusan      = 'D4 Komputasi Statistik'
    tahunLulus   = 2024
    phoneNumber  = "0812999$suffix"
    alamat       = "Jl. Alumni No.$suffix, Depok"
} | ConvertTo-Json -Depth 3

$updatedProfile = Invoke-ApiRequest -Method PUT -Uri "$BaseUrl/api/users/profile" -Headers $authHeader -Body $updateProfilePayload
Write-Output ($updatedProfile | ConvertTo-JsonString)

Write-Host "==> [6/$totalSteps] Pastikan ada institusi untuk relasi riwayat kerja"
$institutionsResponse = Invoke-ApiRequest -Method GET -Uri "$BaseUrl/api/institutions?page=0&size=5" -Headers $authHeader
$institutionList = @()
if ($institutionsResponse.data -and $institutionsResponse.data.content) {
    $institutionList = $institutionsResponse.data.content
}
Write-Output ($institutionList | ConvertTo-JsonString -Depth 5)

$institutionId = $null
if ($institutionsResponse.data -and $institutionsResponse.data.content) {
    $content = @($institutionsResponse.data.content)
    if ($content.Count -gt 0) {
        $institutionId = $content[0].id
    }
}

if (-not $institutionId) {
    Write-Host 'Daftar institusi kosong; membuat institusi demo baru'
    $institutionPayload = @{
        name        = "Institusi Demo $suffix"
        type        = 'SWASTA'
        province    = 'DKI Jakarta'
        city        = 'Jakarta Pusat'
        address     = "Jl. Kantor No.$suffix, Jakarta"
        description = 'Institusi sementara untuk uji coba skenario'
    } | ConvertTo-Json -Depth 3

    $createInstitutionResponse = Invoke-ApiRequest -Method POST -Uri "$BaseUrl/api/institutions" -Headers $adminAuthHeader -Body $institutionPayload
    Write-Output ($createInstitutionResponse | ConvertTo-JsonString)

    $institutionId = $createInstitutionResponse.data.id
    if (-not $institutionId) {
        throw 'Gagal mendapatkan id institusi baru'
    }
    $institutionCreated = $true
    $createdInstitutionId = $institutionId
    Write-Host "Institusi demo dibuat dengan id=$institutionId"
} else {
    Write-Host "Menggunakan institutionId=$institutionId dari data yang sudah ada"
}

Write-Host "==> [7/$totalSteps] Tambahkan riwayat pekerjaan baru"
$workHistoryPayload = @{
    institutionId = [int64]$institutionId
    position      = 'Data Analyst'
    employmentType = 'SWASTA'
    startDate     = '2022-08-01'
    endDate       = '2023-12-31'
    description   = 'Menganalisis data statistik dan membuat dashboard.'
    isCurrentJob  = $false
} | ConvertTo-Json -Depth 3

$workHistoryResponse = Invoke-ApiRequest -Method POST -Uri "$BaseUrl/api/work-histories" -Headers $authHeader -Body $workHistoryPayload
Write-Output ($workHistoryResponse | ConvertTo-JsonString)

$workHistoryId = $workHistoryResponse.data.id
if (-not $workHistoryId) {
    throw 'Gagal mendapatkan id riwayat pekerjaan'
}

Write-Host "==> [8/$totalSteps] Tandai pekerjaan sebagai aktif dengan promosi"
$workHistoryUpdatePayload = @{
    institutionId = [int64]$institutionId
    position      = 'Lead Data Analyst'
    employmentType = 'SWASTA'
    startDate     = '2022-08-01'
    description   = 'Dipromosikan menjadi lead dan memimpin tim analisis.'
    isCurrentJob  = $true
} | ConvertTo-Json -Depth 3

$updatedWorkHistory = Invoke-ApiRequest -Method PUT -Uri "$BaseUrl/api/work-histories/$workHistoryId" -Headers $authHeader -Body $workHistoryUpdatePayload
Write-Output ($updatedWorkHistory | ConvertTo-JsonString)

Write-Host "==> [9/$totalSteps] Tinjau riwayat pekerjaan & statistik dasbor"
$workHistories = Invoke-ApiRequest -Method GET -Uri "$BaseUrl/api/work-histories" -Headers $authHeader
Write-Output ($workHistories.data | ConvertTo-JsonString -Depth 5)

$statistics = Invoke-ApiRequest -Method GET -Uri "$BaseUrl/api/statistics/overview" -Headers $authHeader
Write-Output ($statistics | ConvertTo-JsonString)

Write-Host "==> [10/$totalSteps] Hapus riwayat pekerjaan uji"
$deleteWorkHistory = Invoke-ApiRequest -Method DELETE -Uri "$BaseUrl/api/work-histories/$workHistoryId" -Headers $authHeader
Write-Output ($deleteWorkHistory | ConvertTo-JsonString)

if ($institutionCreated -and $createdInstitutionId) {
    Write-Host "==> [11/$totalSteps] Hapus institusi demo yang tadi dibuat"
    $deleteInstitution = Invoke-ApiRequest -Method DELETE -Uri "$BaseUrl/api/institutions/$createdInstitutionId" -Headers $adminAuthHeader
    Write-Output ($deleteInstitution | ConvertTo-JsonString)
} else {
    Write-Host "==> [11/$totalSteps] Melewati penghapusan institusi karena menggunakan data yang sudah ada"
}

Write-Host "==> [12/$totalSteps] Hapus akun uji (status menjadi INACTIVE)"
$accountDeletePayload = @{
    password     = $alumniPassword
    confirmation = 'DELETE'
} | ConvertTo-Json

$deleteAccount = Invoke-ApiRequest -Method DELETE -Uri "$BaseUrl/api/users/account" -Headers $authHeader -Body $accountDeletePayload
Write-Output ($deleteAccount | ConvertTo-JsonString)

Write-Host 'Skenario selesai. Token alumni tidak berlaku lagi setelah akun dinonaktifkan.'
