# orbital-android

Orbital is an app that lets you create livecasts on the VideoCoin Network. It showcases how easy it is for developers to start using the VideoCoin network for a video streaming app/project.

Read more about the VideoCoin network API here: https://docs.videocoin.network/

## Environment
1. Android Studio 3.5 or later
2. Android NDK version r20b or later. Android Studio should automatically locate your NDK folder. If Android Studio does not automatically locate your NDK folder, update the `ndk.dir` property in the `local.properties` files to reflect this path
  
## Setup
The app uses Firebase as its backend and requires a Firebase project set up with a `google-services.json` config file included in the project to complete the build successfully.

### Download google-services.json file
1. Open the Firebase console: https://console.firebase.google.com/
2. Create a new Firebase project
3. Add an Android app to the project. Note the 'Android package name' value. It will be used in step 8
4. Download the resulting `google-services.json` file and put it in `app` folder of your project
5. The 'Add Firebase SDK' step has already been implemented in the Android project's Gradle files and can be skipped
6. More configuration is needed before the app is ready to be run. Skip the 'Run your app to verify installation' step for now
7. Open Android Studio and import the project
8. Open the app level `build.gradle` file and update the `applicationId` value to the value set to 'Android package name' specified in step 3
9. Rebuild the project

### Configure Firebase Authentication
1. Navigate to your Firebase project
2. Navigate to the 'Settings' page of your project and scroll down to the 'Your apps' section
3. Add the SHA1 certificate fingerprint of the Android project and click 'Save'. This can be obtained by running the `signingReport` Gradle task  
    a. This can be found by opening the Gradle window at the right of the Android Studio IDE and navigating to $PROJECT_NAME (root)->Tasks->android->signingReport. Double-clicking `signingReport` task generates the report displaying the SHA-1 value.
4. Navigate to the 'Authentication' page of your Firebase project and enable the Google sign-in provider under the 'Sign-in method' tab

### Create VideoCoin API key and configure Firebase RemoteConfig
1. To stream using the VideoCoin network, create a publisher account here: https://studio.videocoin.network/
2. Once the VideoCoin publisher account is created, navigate to the 'Account' page and create a new API token. This will be used in step 4
3. Verify the publisher account associated with the API token has at minimum 20 VID and no more than 50 VID. Accounts funded with less than 20 VID or more than 50 VID will receive an error when attempting to create a stream
4. Navigate to your Firebase project
5. Open the 'Grow' section and open the 'Remote Config' page. Add a new parameter with the key `API_KEY` and set its value to the VideoCoin API token obtained in step 2 

### Configure Firebase Cloud Firestore
1. Navigate to your Firebase project
2. Open the 'Database' page and click 'Create database'
3. Follow the instructions given by Firebase to complete the database creation
4. Open the newly created database and select the 'Indexes' tab. Create a new index with **exactly** the values given below:  
    a. Set 'Collection ID' to `videos`  
    b. Add a `status` field and set to Ascending  
    c. Add a `created_at` field and set to Descending  
    d. Set 'Query scopes' to 'Collection'  

Your app is now ready to be loaded to an Android device for testing!

## Firebase usage
Refer to the Firebase docs for additional information on Firebase: https://firebase.google.com/

The following components of Firebase are used.
* Firebase Remoteconfig (https://firebase.google.com/products/remote-config/): to retrieve api_key to stream to the VideoCoin network
* Firebase Cloud Firestore (https://firebase.google.com/products/firestore/): to store data of livestreams being created
* Firebase Cloud Storage (https://firebase.google.com/products/storage/): to store the thumbnails for the videos

### Libraries used
* yasea - android streaming client
* exoplayer - media player to watch HLS streams

## Reporting issues
Report issues to our GitHub issue tracker. Please read our [reporting issue guidelines](.github/reporting_issues.md) before opening a new issue
