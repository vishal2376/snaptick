# Changelog

All notable changes to this project will be documented in this file.

## [2.0] - 2024-03-10

### 🐛 Bug Fixes

- Empty list issue
- Wrong worker class
- Repeat task invalid taskId issue
- Data reset on rotation by disabling orientation #35
- No task inserted in db if reminder is false
- Remind schedule for 12:00 AM
- Low opacity and flicker on reset button
- Hardcoded color
- Nav item tint color
- Hardcoded color of icon tint
- PriorityComponent text contrast
- Touch not working during snackbar msg
- Whitespace title issue
- Wrong weekday title
- Nav issue
- Padding issue
- Drawer remain opened after thisweek screen nav
- Task edit not working
- Touch ripple issue
- String and translation issue
- Wrong tasks list issue
- Theme indicator not updating default value
- Ripple box issue
- Non-repeated tasks not showing

### ⚙️ Miscellaneous Tasks

- Add weekdays ui
- Improve WeekDaysComponent
- Add notification sound,vibration and light
- Modify task model - add pomodoro time and weekdays
- Refactor enum classes
- Add room migration
- Updated db schema and add migration
- Fix migration issue
- Modify pomodoro default value
- Add surface color as accent color
- Impl checkbox in WeekDaysComponent
- Impl extension function
- Return string instead of weekday list
- Impl weekdays in add screen
- Add weekDay component in add/edit screen
- Add repeat task worker
- Impl new repeat task system
- Add query to load recentRepeatedTasks only
- Improve delay and refactor worker class
- Cancel older notification before new request
- Converted hardcoded text into string resource #18
- Add setting screen route
- Design basic SettingsScreen UI
- Remove addition padding in top nav bar
- Change insert to update task and using simple enqueue worker
- Improve delay of repeat ^ change notification enqueue
- Change query to extract repeated tasks
- Add keep screen on feature (#41)
- Add monochrome icon (#29)
- Add light theme colors
- Add light theme color scheme
- Reduce shadow and fix icon tint in light mode
- Refactor code + improve UI
- Add animation in snackbar
- Refactor color for task component
- Change snackbar optional actiontext
- Remove extra topbar padding
- Add upcoming day option
- Add indicator of weekdays
- Add sorted list
- Add repeated weeklist title
- Create This Week task screen
- Refactor location
- Add route and impl thisweekscreen in nav
- Impl onClick nav
- Add nav bar item "this week"
- Refactor code
- Only show today tasks in homescreen
- Update theme update func
- Refactor app theme enum
- Add setting dataStore file
- Impl new settings data store
- Add day of week indicator in this week
- Rename dummy tasks var

### Add

- Weekday enum + its converter class

### Upgrade

- AppVersion to 2.0

## [1.1.2] - 2024-02-21

### 🐛 Bug Fixes

- Streak not updating issue #20, already completed repeated task issue #22

### 📚 Documentation

- Add figma link

### ⚙️ Miscellaneous Tasks

- Refactor priority colors
- Add repeat icon in task #21

### Upgrade

- AppVersion to 1.1.2

## [1.1.1] - 2024-02-20

### 🐛 Bug Fixes

- Screen cropped issue #15

### 📚 Documentation

- Add izzyOnDroid button

### Upgrade

- AppVersion to 1.1.1

## [1.1] - 2024-02-19

### 🐛 Bug Fixes

- Repository injection issue
- Notification even after task deleted
- Wrap text issue in small screen (issue #12)

### 📚 Documentation

- Update screenshots and features
- Add google play button
- Update features

### ⚙️ Miscellaneous Tasks

- Add repeat switch in Add Screen
- Impl worker class for repeated tasks
- Add schedule task funcion & refactor code
- Add schduleRepeatTask in updateTask
- Created custom swipe action
- Impl swipe action in HomeScreen
- Impl onSwipe action Event
- Remove worker class
- Remove extra constants
- Add gson parsing for task
- Refactor repeat task logic
- Remove repeat task schedule methods
- Add validation for repeated tasks
- Improve logic and fix negative delay time
- Add repeat daily option in add/edit screen
- Impl onUpdate task Repeat
- Improve load today tasks logic

### Add

- Metadata(in fastlane structure)
- Gson library

### Upgrade

- AppVersion to v1.1

## [1.0] - 2024-02-14

### 🚀 Features

- Add time formatting func
- Impl nav routes and handle task list
- Add random color
- Impl bottomsheet to add task
- Impl confirm delete task
- Check/uncheck task
- Add priority options
- Add completed task screen
- Add CustomCircularProgressBar
- Add pomodomoro screen
- Impl basic timer
- Impl formatted time with acutal duration
- Impl timer with progress bar
- Reset button
- Slow flicker effect in pomodoro
- Impl amoled theme
- Impl notification with channel
- Add preference manager class
- Impl acra for automate crash reports
- Cancel notification

### 🐛 Bug Fixes

- Negative time difference
- Large fontsize
- Homescreen add/edit function issues
- Wrong route
- Start and end time value not updated
- Show wrong time
- Not storing getTaskById value
- Time picker value not updating
- Wrong string in title and button
- Fontsize and spacing
- Missing arguments
- Task list ui not updated on task complete
- Multiple getTaskById calls issue
- Annoying underline indicator
- Task textfield style
- Reminder is not sync with viewmodel task
- Timer not centered
- Restricted timer bug
- Theme colors issue
- Constant defaultSortTask
- Forget to update appState
- List is not updating on sort
- Wrong indicator selected
- Minor change
- Invalid pop backstack
- App pref save and load not working
- App pref save and load not wokrking
- Show wrong time and priority onEditTask
- *(pomodoro_screen)* Show wrong time and priority
- Theme switch button not working
- Flicker effect when reset timer and  timer text alpha not resetting
- Wrong exception
- Topbar bg color delay
- Free time count past task durations
- LocalDate is not working in DB Room
- 0 free time issue
- Timer not recompose on mutable var change
- Timer recompose issue in edit screen
- Forget to omit past task duration in task analysis
- Wrong custom duration at init
- Dull color on startup
- Schedule notification even task finished
- Bg color issue
- Streak go brrrr
- StartTime not updated in editScreen
- Duration condition and endTime update issue
- Set reminder in past time
- Priority unequal box
- Analyze empty tasks
- Show notification even task is already done

### 🚜 Refactor

- Task models
- Repository
- Make AddTaskScreen stateless
- Change event class naming convention
- Taskviewmodel location
- Change func name
- Remove testing code
- Add display text
- Sort tasks
- Change view model location
- FreeTime logic
- DurationComponent location

### 📚 Documentation

- Update features
- Update features
- Update sortBy methods
- Add LICENSE
- Add app logo and credits

### ⚙️ Miscellaneous Tasks

- Modify theme colors
- Remove hardcoded strings
- Add floating button
- Setup hilt application
- Add task dao interface
- Add room database
- Add repository class
- Add app module
- Add task viewmodel
- Design empty task screen ui
- Fix ui and remove hardcoded strings
- Create add/edit screen
- Add viewmodel,back navcontroller and title logic
- Add update button func
- Replace bottomsheet with new screen
- Add screen done
- Add update function
- Edit screen done
- Add,edit task screen nav compose added
- Remove bottomsheet
- Replace Long with LocalTime
- Open/close reminder bottomsheet
- Update reminder in task
- Change button name
- Remove multiple reminder system
- Change blue color
- Add task priority in model
- Add task priority in model
- Add priority option and fix reminder update
- Show reminder icon in list
- Fab color change
- Remove viewmodel dependency from home screen
- Add edit screen event and made stateless compose
- Impl add_edit screen event
- Impl nav to completion screen
- Add animation in task listi
- Impl pomodoro screen navigation
- Improve the design
- Impl reset button
- Add marquee effect on large title
- Only apply flicker anim to text instead of Box
- *(pomodoro screen)* Impl button complete task
- Add roboto mono font
- Add dark color for amoled screen
- Add navigation drawer and switch button
- Add menu button
- Improve list animation speed
- Add animation in info cards
- Add animation in pomodoro timer
- Add sort icon
- Impl sort dialog
- Impl sort task
- Add vibrator permission
- Vibrate and change text when task is completed
- Add some debug
- Created custom pie chart ui
- Add trimSeconds option
- Created pie chart item ui
- Change title and add onBack func
- Add freetimescreen in nav route
- Add onClick event in homescreen
- Improve ui & add marquee
- Add progress animation
- Add pie chart item animation
- Improve list animation
- Impl free time logic
- Impl rotational animation
- Impl free time logic with decimal accuracy
- Add priority colors list
- Add indicator in priority choice
- Refactor code of priority
- Add indicator animation
- Add formatted time in hours and min
- Impl PreferenceManager save and load
- *(EditTaskScreen)* Code cleanup
- *(AddTaskScreen)* Code cleanup
- *(PomodoroScreen)* Refactor + code cleanup
- Change bg to primary color
- Change default theme
- Add testing button
- Add deleteAllTasks func
- Rename createNotification to showNotification
- Add worker class to handle schedule notifications
- Impl schedule notification and workmanager enqueue
- Improve free time logic
- Add task validation
- Add isOptional flag + validate task in EditTaskScreen
- Add 1 minute in startTime and endTime
- Change default pref
- Add 2 more sort options + removed sortBy title
- Add totalTimeDuration to validate time more accurately
- Change app logo
- Add app logo svg
- Add more nav drawer items
- Fix logo spacing
- Impl nav drawer item func
- Add buildVersion code
- Minor color improvement
- Add date for task
- Add getTasksByDate in repository class and dao interface
- Add duration button ui
- Duration ui component done
- Add durationcomponent in add/edit screen
- Add durationList in appState
- Return duration instead of index
- Add duration logic for endTime
- Add formatted duration
- Default duration in add/edit Screen
- Add animation to recompose smoothly
- Add isRepeated to model Task
- Generate Dummy Tasks
- Add uuid for notification id
- Impl custom duration
- Add task streak
- Improve dialog UI
- Impl streak logic
- Impl splash screen with animation
- Remove log statements
- Remove startTime validation
- Improve validation and duration logic
- Add releases to .gitignore
- Increase  default startTime to 5min
- Update reminder notification when update OnComplete Task

### Add

- Fonts,colors and icons
- Text styles
- Infocard and homescreen
- Task,infocard component and homescreen UI
- Task entity
- Task entity
- Type LocalTime converter
- Reminder list convertor
- Timer icon in TaskComponent
- Material extended icons lib
- Func to vibrateDevice
- Free time screen
- FreeTimeScreen UI
- Roboto_mono.ttf
- Logo for both theme

### Update

- Task table
- AppVersionName to 1.0-beta

### Upgrade

- AppVersion to 1.0

<!-- generated by git-cliff -->
