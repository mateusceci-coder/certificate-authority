
package com.example.demo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private String serialNumber;
    private String version;
    private String commonName;
    private String organizationName;
    private String organizationalUnit;
    private String country;
    private String state;
    private String locality;
    private String email;
    private String notBefore;
    private String notAfter;
    private String status;
}
