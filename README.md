TODO Project Backend
Description
This backend application provides the necessary APIs for managing tasks in the TODO project. Users can perform CRUD (Create, Read, Update, Delete) operations on tasks, authenticate with JWT tokens, and receive email notifications for tasks due soon. The backend also supports role-based access control, allowing user promotion to admin roles and management of privileges.

Features
User Authentication:
JWT-based authentication.
Users can log in using either email or username.
Registration:
Register users with USER or ADMIN roles.
CRUD Operations for Tasks:
Add, edit, delete, and retrieve tasks.
Search Tasks:
Search tasks by title.
Mark Tasks:
Mark tasks as important or completed.
Role-based Access:
Users with the ADMIN role can promote other users and manage their tasks.
Email Notifications:
Send email reminders for tasks due within 24 hours.
Task Filtering:
Retrieve tasks by status (completed, important).
API Response:
Unified API response format using CommonApiResponse for consistent response structure.

