# Subway Pregnant Seat Reservation Application
> Reserve a pregnant woman's seat through the status of the pregnant woman's seat by train and real-time subway arrival information

![Dev][dev-image]
- Authenticate a pregnant woman by entering a virtual pregnant woman number.
- Select the subway station and choose the train you want to take.
- Check the remaining pregnant women on the train and reserve the pregnant women.

![pregnantApp](https://user-images.githubusercontent.com/25261274/76843888-cf85a900-687f-11ea-94c0-e550266af528.gif)

## Features
- Using the public data portal's real-time arrival information API
- Create a virtual health center server and a pregnant seat management server to provide pregnant women authentication and pregnant seat reservation services.

## Demo

## How to test
- Run after 'New_EX_Application' import through Android studio
- Enter the test pregnant woman number for authentication.
  - _'111111111'_
  - _'222222222'_
  - _'333333333'_
  - _'444444444'_

## How to Install and Run
- `mysql/project_1.sql` import in Mysql
- Copy files within `www/html/` in Apache home directory
  - Modify the contents of `dbcon.php` to fit your Mysql account
- `New_EX_Application` import to Android studio
  - Modify the server ip address of the *IP_ADDRESS* variable in `MainActivity.java`

## Development environment
- Apache 2.4
- php 5.6
- mysql 5.5
- Android Studio 3.5
  - Android Studio Gradle 4.6
  - Android Studio Gradle Plugin 3.2.1
- Amazon Linux


<!-- Markdown link & img dfn's -->
[dev-image]: https://img.shields.io/badge/Dev-Android-green
