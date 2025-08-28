const DEFAULT_API_URL = "/api";

export const api = {
  getCaInfo: async () => {
    try {
      const response = await fetch(`${DEFAULT_API_URL}/ca-info`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const data = await response.json();
      console.log("getCaInfo", data);
      return data;
    } catch (error) {
      console.error(error);
      return null;
    }
  },
  getBySerialNumber: async (serialNumber: string) => {
    try {
      const response = await fetch(
        `${DEFAULT_API_URL}/certificates/${serialNumber}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      return response.json();
    } catch (error) {
      console.error(error);
      return null;
    }
  },
  issueCertificate: async (certificate: string) => {
    try {
      const response = await fetch(`${DEFAULT_API_URL}/issue-certificate`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(certificate),
      });
      return response.json();
    } catch (error) {
      console.error(error);
      return null;
    }
  },
  validateSignature: async (signature: string) => {
    try {
      const response = await fetch(`${DEFAULT_API_URL}/validate-signature`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ signature }),
      });
      return response.json();
    } catch (error) {
      console.error(error);
      return null;
    }
  },
};
