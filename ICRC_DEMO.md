# Modification Done

Some small modifications to:
- be able to compile the project ( JDK compatibility issues)
- use OpenMRS dev3 serveur. See `com.google.android.fhir.demo.FhirApplication.onCreate`. the default password has been hardcoded as it's  publicly available.


# How to test it
By default, only patient City address `NAIROBI` will be displayed. Hardcoded in the demo in  `com.google.android.fhir.testing.TestDownloadManagerImpl`


Start demo with your preferred IDE ( Intellij, Android Studio)

# Demo

## Add user on OpenMRS

Create some patients on https://dev3.openmrs.org with the city NAIROBI:
![How to create a user on OpenMRS](./icrc-documents/create-user.png)


For instance, the user `FHIRDemo ICRC` has been created: 
![user FHIRDemo](./icrc-documents/user-created.png)


## Find this user on the mobile App

on the mobile app, click on "Sync", to see all patients with city address `NAIROBI`
![users found on mobile app](./icrc-documents/users-found-on-mobile.png)

Click on the demo patient to check data are synchronized:
![users found on mobile app](./icrc-documents/user-demo.png)


## Editing a patient Details

Synchro is not working currently.

