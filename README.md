# API Pelacakan Alumni STIS

Layanan web RESTful untuk mengelola profil alumni STIS, riwayat pekerjaan, institusi, dan statistik dasbor. Layanan ini mengikuti spesifikasi di `Specification.md`, termasuk autentikasi JWT, kontrol akses berbasis peran, dan endpoint CRUD yang lengkap.

## Teknologi

- Java 21
- Spring Boot 3
- Spring Data JPA dengan MariaDB
- Spring Security dengan autentikasi JWT
- springdoc-openapi untuk dokumentasi Swagger
- Sistem build Maven

## Memulai

1. **Prasyarat**
   - Java 21
   - Maven 3.9+
   - MariaDB 11.x (koneksi bawaan: `jdbc:mariadb://localhost:3306/tracking_alumni`)

2. **Konfigurasi**
   - Perbarui `src/main/resources/application.yml` dengan kredensial basis data dan secret JWT Anda.

3. **Jalankan aplikasi**
   ```bash
   mvn spring-boot:run
   ```

4. **Dokumentasi API**
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI docs: `http://localhost:8080/v3/api-docs`

### Alur Autentikasi
- `POST /api/auth/register` — membuat akun pengguna.
- `POST /api/auth/login` — mendapatkan token JWT (Bearer).
- Sertakan `Authorization: Bearer <token>` untuk seluruh endpoint yang diamankan.

### Endpoint Utama
- **Profil Pengguna**: `/api/users/profile`, `/api/users/change-password`, `/api/users/account`.
- **Manajemen Alumni (Admin)**: `/api/alumni`, `/api/alumni/{id}`.
- **Riwayat Pekerjaan**: endpoint CRUD di `/api/work-histories`.
- **Institusi**: `/api/institutions` untuk CRUD (operasi admin) dan `/api/institutions/options` untuk pencarian.
- **Dasbor & Statistik**: `/api/dashboard/*`, `/api/statistics/*`.

Lihat `Specification.md` untuk skema payload lengkap dan detail perilaku.

## Pengujian

Jalankan seluruh pengujian:

```bash
mvn test
```

## Struktur Proyek

- `com.stis.alumni.entity` — entitas JPA.
- `com.stis.alumni.dto` — DTO untuk request/response.
- `com.stis.alumni.service` — logika bisnis.
- `com.stis.alumni.controller` — controller REST API.
- `com.stis.alumni.security` — konfigurasi JWT dan Spring Security.
- `com.stis.alumni.config` — konfigurasi umum termasuk Swagger.
- `Specification.md` — requirement sistem asli.

## Catatan

- Password disimpan menggunakan hashing BCrypt.
- Token JWT memiliki masa berlaku bawaan 24 jam; ubah melalui `app.jwt.expiration-seconds`.
- Penghapusan institusi yang masih terhubung ke riwayat pekerjaan diblokir demi menjaga integritas referensial.
