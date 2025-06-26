video link: https://youtube.com/shorts/MRH53fozgf4?si=x48mb7u4PeQQHdHO

SmartSpend App – Project Report 

 1. Purpose of the Application
SmartSpend is a modern personal finance management app built for Android, combining the proactive budgeting of YNAB with the automation and insights of Buxfer. It empowers users to track spending, set savings goals, manage debts, and visualize financial health—from anywhere, even offline.
 Core Objectives
•	Enable real-time tracking of income and expenses.
•	Support multiple budgeting styles (category-based, zero-based).
•	Provide smart financial insights and forecasting.
•	Offer offline-first usage with seamless Firebase synchronization.
•	Ensure cross-device access and data privacy.

 2. Design Considerations
SmartSpend is developed using Android Studio with Kotlin, following Material Design 3 principles.
 User Interface (UI)
•	Card-based layout using CardView for dashboards and key financial areas.
•	Bottom Navigation + Navigation Drawer for intuitive navigation.
•	Visual cues: charts (MPAndroidChart), progress bars, and color-coded badges.
•	Dynamic components: Quick Add button, user avatar, real-time budget bars.
 User Experience (UX)
•	Minimal clicks to perform key actions (add expense, set goal, etc.).
•	Smooth onboarding (landing screen → sign up/login via Firebase).
•	Context-aware tips, tooltips, and alerts.
•	Adaptive UI for dark mode, large text, and accessibility.
 Architecture
 Bill reminder push notification
•	Modular structure with Activities and Fragments
•	MVVM pattern (planned or implemented) for clean state management
•	Firebase as the backend (authentication, Firestore/Realtime DB, storage)

 4. Firebase Integration
SmartSpend is fully integrated with Firebase to enhance functionality, performance, and scalability.
 Features Enabled
Firebase Feature	Usage
Firebase Authentication	Secure user login & signup with email/password
Cloud Firestore	Store and sync user expenses, budgets, goals
Firebase Storage	Profile images, attachments (optional)
Firebase Analytics	Track user engagement and usage flow
Firebase Cloud Messaging	Send financial tips, alerts, or reminders
Firebase Crashlytics	Monitor and debug crashes and app issues
 Authentication Flow
•	On app launch, users are checked against Firebase Auth.
•	If not logged in, users can sign in or sign up securely.
•	Firebase handles session persistence and token refresh.
 Data Sync Flow
•	Data entered offline is saved locally (RoomDB or cache).
•	When online, the app pushes updates to Cloud Firestore.
•	Real-time sync across devices ensures consistent data views.


 5. GitHub Utilization
GitHub is used for source code hosting, issue tracking, and collaborative development.
 Repository Structure
/app/src/main/java/      → Kotlin source files  
/app/src/main/res/       → Layouts, drawables  
/.github/workflows/      → GitHub Actions CI/CD workflows  
/firebase/               → Firebase config files  
 Branch Strategy
•	main → Production-ready, Firebase-linked code
•	dev → Integration testing with Firebase updates
•	feature/* → Per-feature development (e.g., feature/firebase-login)

 6. GitHub Actions (CI/CD)
GitHub Actions automates project workflows to maintain code quality and speed up development.
🛠 Workflows in Use
 Build and Test
- Checkout
- Set up JDK
- Build APK (assembleDebug)
- Run Unit Tests
 Firebase Integration Checks (Optional Add-on)
You can add:
- name: Deploy Firestore rules
  run: firebase deploy --only firestore:rules
 APK Build and Artifact Upload
- name: Upload APK
  uses: actions/upload-artifact@v3
  with:
    name: debug-apk
    path: app/build/outputs/apk/debug/app-debug.apk

 6. Security & Privacy Considerations
•	All user data is secured via Firebase Auth.
•	Firestore rules restrict access to user-owned documents.
•	Option to use biometric authentication or PIN locks.
•	Minimal permissions: No unnecessary access to contacts, SMS, etc.

7. Key Features Implemented
Feature	Status
User authentication (Firebase)	 Completed
Expense tracking	Completed
Budget setting and reminders	 Completed
Pie chart dashboard	 Completed
Real-time sync via Firebase	 Completed
Data visualization	 Completed
Settings & preferences	 Completed
Bill Reminder Completed

 9. Future Enhancements
•	Firebase Dynamic Links for sharing financial goals
•	Firebase Functions for server-side logic and summarizations
•	In-app purchase (IAP) support for premium financial tools
•	AI chatbot for financial advice (integrated via Firebase ML or third-party)

Summary
Category	Details
App Name	SmartSpend
Platform	Android (Kotlin)
Backend	Firebase (Auth, Firestore, Storage, FCM)
CI/CD	GitHub Actions
Version Control	GitHub
Design	Material Design 3, card-based UI, modern UX
Security	Firebase Rules, biometric login, encrypted storage planned
Users	Budgeters, students, small business owners, individuals with debt/saving goals




