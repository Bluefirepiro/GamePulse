Introduction:

Gamepulse is an app made to help gamers and already busy users try to find time to play games they enjoy, track the time it takes to beat it
the amount of tiem they have spent on the game, as well as recently played games and recently earned achievements, across multiple
platforms.

Features:

This app will allow users to track games from Xbox and Steam (for now) using just the one app instead of trying to log into multiple accounts
to see their game library and achievements. It will also help the user schedule time to play their favorite games or give them
a notification when they are able to play between their busy schedules. 

Technologies:

We are using android studio to build this app, it will only function on an andriod device but there could be future IOS devices added
the APIs that will be used are XAPI (Xbox Live REST) and Steam Web API.

Installation:

This will be a simple app that can be installed to any android device that is using android 14 or later.

Development Setup

Welcome to the GamePulse development setup guide! This section will help new developers set up their environment to work on the project and create an initial build. Follow the steps below to get started.
1. System Requirements

Before you begin, ensure that your development environment meets the following minimum requirements:

    Operating System: Windows 10/11, macOS, or Linux
    RAM: 8 GB (16 GB recommended)
    Storage: At least 10 GB of free space
    Processor: Intel i5 or higher
    Android SDK: Installed with necessary packages
    Java Development Kit (JDK): Version 8 or higher

2. Prerequisites

Make sure you have the following software installed:

    Android Studio (latest stable version recommended)
    Java Development Kit (JDK) - Make sure it's correctly configured in your system’s environment variables.
    Git - For cloning the repository and version control.

3. Clone the Repository

4. Open the Project in Android Studio

    Open Android Studio.
    Select File > Open.
    Navigate to the cloned gamepulse directory and open it.
    Android Studio will automatically start syncing the project and downloading the necessary Gradle dependencies. This may take a few minutes.

5. Configure the SDK and Emulator

    SDK Configuration:
        Go to File > Project Structure > SDK Location.
        Make sure the SDK path is correct (e.g., C:\Users\YourName\AppData\Local\Android\Sdk on Windows).
        Ensure that the appropriate API level (e.g., API 30 or 31) is installed.

    Set Up an Emulator (Optional but recommended):
        Go to Tools > AVD Manager.
        Create a new virtual device, choose a device definition, and select the appropriate system image.
        Click Finish to create the emulator.

6. Building the Project

    Sync Gradle:
        In Android Studio, make sure that Gradle syncs successfully. If you encounter issues, check the build.gradle files for any missing dependencies.

    Clean and Rebuild:
        To ensure that the project is set up correctly, run Build > Clean Project followed by Build > Rebuild Project.

    Run the Application:
        Connect an Android device via USB or start the Android emulator.
        Click the Run button (green arrow) in Android Studio, or use the shortcut Shift + F10.

7. Common Issues and Troubleshooting

    Gradle Sync Issues: Ensure you are connected to the internet, as Android Studio may need to download additional dependencies.
    Emulator Performance: If the emulator is slow, consider enabling hardware acceleration (HAXM on Intel-based systems).
    SDK Path Errors: Ensure the SDK path in the project settings matches the installed location.

8. Coding Guidelines

    Follow Kotlin Coding Conventions: Make sure to adhere to the standard Kotlin coding conventions.
    Use AndroidX Libraries: Ensure that any new libraries or updates use AndroidX.
    Document Your Code: Use comments and Kotlin's KDoc to document your code where necessary.

9. Contributing

If you plan to contribute to the project:

    Create a new branch for your feature or bug fix.
    Make your changes.
    Push the branch to the repository.
    Open a Pull Request with a description of your changes.

10. Building the APK

To create a signed APK:

    Go to Build > Build Bundle(s)/APK(s) > Build APK(s).
    Follow the instructions to sign and build the APK.
    The APK will be located in the app/build/outputs/apk/ directory.
License: 

    Copyright (c) <2024> <Scott Parrillo & Christian Davis>. All rights reserved.

    Redistribution and use in source and binary forms are permitted provided that the above copyright notice and this paragraph are duplicated in all such forms and that any documentation, 
    advertising materials, and other materials related to such distribution and use acknowledge that the software was developed by the <copyright holder>. 
    The name of the <copyright holder> may not be used to endorse or promote products derived from this software without specific prior written permission. 
    THIS SOFTWARE IS PROVIDED `'AS IS″ AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, 
    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.


Contributors:

  Scott Parrillo
  Christain Davis

Project Status:
  
Pre-Alpha
