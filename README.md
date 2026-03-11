# Genny_Report

Reminder: 
Before Running the software edit those .json config files to setup the database connection and Header for the PDF report file.

Database config accepted formats in dataBaseConfig.json:

1. Full JDBC URL

{
	"url": "jdbc:postgresql://HOST:5432/DB_NAME",
	"user": "DB_USER",
	"passwd": "DB_PASSWORD"
}

2. Host/port/database shortcut

{
	"url": "HOST_OR_IP",
	"port": "5432",
	"database": "DB_NAME",
	"user": "DB_USER",
	"passwd": "DB_PASSWORD"
}

Currently accepting help in fixing the problem where characters with accents are not displayed correctly.
this problem its happening only in the jar version. If you run the software via IDE the problem do not occur.
