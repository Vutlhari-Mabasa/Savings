video link: https://youtube.com/shorts/6Xb65Xs2jf0?si=8ekbRF8Uky6tD5HG

# SmartSpend – Mobile Budgeting App


## Purpose of the Application

**SmartSpend** is a mobile budgeting app that blends the principles of YNAB (You Need a Budget) with the automation and forecasting power of Buxfer. It helps users track spending, set goals, manage debts, and forecast their financial health — both online and offline.

### Core Objectives

- Track income, expenses, debts, and savings goals.
- Support multiple budgeting styles (zero-based, category-based).
- Offer offline-first usage with Firebase synchronization.
- Provide smart financial insights and goal tracking.
- Enable cross-device access with strong privacy and security.


## Design Considerations

### User Interface (UI)

- Modern card-based layout using Material Design 3.
- Bottom navigation + drawer menu for easy access.
- Responsive to dark mode and accessibility settings.
- Dynamic charts (MPAndroidChart) and progress visuals.

### User Experience (UX)

- Clean onboarding flow with Firebase login.
- QuickAdd expense button.
- Tips and alerts based on usage.
- Minimal friction to complete tasks.



## Firebase Integration

| Feature                  | Purpose                                    |
|--------------------------|--------------------------------------------|
| Firebase Auth            | User login & signup                        |
| Firestore                | Store and sync transactions, budgets       |
| Firebase Analytics       | Track usage flow and feature adoption      |
| Firebase Messaging       | Tips, reminders, financial nudges          |
| Firebase Crashlytics     | Crash monitoring and debugging             |

### Authentication Flow

- Firebase Auth handles login/signup.
- Persistent sessions across app restarts.
- Email/password or biometric login .

### Sync Flow

- Offline input saved locally.
- When online, syncs to Firestore in real time.



## GitHub & CI/CD

### Repository Structure



/app/src/main/java/     # Kotlin source code
/app/src/main/res/      # Layouts, icons, themes
/.github/workflows/     # GitHub Actions
/firebase/              # Firebase config


###  Branching Strategy

- `main` → Stable production code
- `dev` → Active development
- `feature/*` → Isolated new features

### GitHub Actions

- **Build APK** using Gradle
- **Run unit tests** automatically
- **Lint checks** for quality control
- **Deploy Firestore Rules** (optional)



## Security & Privacy

- Firebase Authentication for secure access
- Firestore rules to prevent unauthorized reads/writes
- Optional biometric/PIN authentication
- Data encrypted in transit (HTTPS)


## Key Features Implemented

| Feature                    | Status        |
|----------------------------|---------------|
| Firebase Auth Integration  |  Completed  |
| Expense Tracking           |  Completed  |
| Budget Setting             |  Completed  |
| Chart-based Dashboard      |  Completed  |
| Offline Data Capture       |  Completed  |
| Real-Time Sync             |  Completed  |


##  Enhancements

- Firebase Cloud Functions for server logic
- Search and filter
- Reminder to  track expenses
- Savings progress bar
- Graph to track expenses via category

  
## Summary

| Aspect             | Details                                  |
|--------------------|------------------------------------------|
| **Platform**       | Android (Kotlin)                         |
| **Backend**        | Firebase (Auth, Firestore, Messaging)    |
| **Design**         | Material 3, responsive card-based UI     |
| **Version Control**| GitHub                                   |
| **Automation**     | GitHub Actions for CI/CD                 |
| **Security**       | Firebase Auth, rules, planned biometrics |



### License & Contribution

> MIT License – Contributions welcome!  
> Pull requests should follow the branch naming convention `feature/your-feature`.


