# Modification Done

Some small modifications to:
- be able to compile the project ( JDK compatibility issues)
- use OpenMRS dev3 serveur. See `com.google.android.fhir.demo.FhirApplication.onCreate`. the default password has been hardcoded as it's  publicly available.


# How to test it
By default, only patient City address `NAIROBI` will be displayed. Hardcoded in the demo in  `com.google.android.fhir.testing.TestDownloadManagerImpl`


Start demo with your preferred IDE ( Intellij, Android Studio)

Create some patients on dev3 with the city NAIROBI.
