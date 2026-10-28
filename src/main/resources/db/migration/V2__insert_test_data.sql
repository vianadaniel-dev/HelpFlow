-- La contraseña para ambos es: 123456 (encriptada con BCrypt)
INSERT INTO users (id, document, corporate_email, password, name, headquarters, area, status)
VALUES ('11111111-1111-1111-1111-111111111111', '1000000000', 'daniel@helpflow.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCG3JZGZcUF.cjRxGv.2m', 'Daniel Viaña', 'BARRANQUILLA', 'Desarrollo', 'ACTIVE');

INSERT INTO technicians (id, document, corporate_email, password, name, headquarters, specialty, support_profile, status)
VALUES ('22222222-2222-2222-2222-222222222222', '2000000000', 'tecnico@helpflow.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCG3JZGZcUF.cjRxGv.2m', 'Soporte IT', 'BARRANQUILLA', 'SOFTWARE', 'LEVEL_2', 'ACTIVE');