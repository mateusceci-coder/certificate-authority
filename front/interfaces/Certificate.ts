export interface Certificate {
  serialNumber: string;
  version: number;
  commonName: string;
  organizationName?: string;
  organizationalUnit?: string;
  country?: string;
  state?: string;
  locality?: string;
  email?: string;
  notBefore: string;
  notAfter: string;
  status: string;
  signatureAlgorithm: string;
  publicKeyAlgorithm: string;
  issuer: string;
}
