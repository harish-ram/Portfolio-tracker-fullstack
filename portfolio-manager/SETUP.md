# Setup Instructions

## Environment Variables

Before running the application, you need to set the following environment variables with your actual credentials:

### Google OAuth Configuration
```bash
GOOGLE_CLIENT_ID=your_actual_google_client_id
GOOGLE_CLIENT_SECRET=your_actual_google_client_secret
```

### MarketStack API Configuration
```bash
MARKETSTACK_API_KEY=your_actual_marketstack_api_key
```

## Setup Steps

### 1. Copy Configuration Template
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

### 2. Set Environment Variables

#### Windows (PowerShell)
```powershell
$env:GOOGLE_CLIENT_ID="your_actual_id"
$env:GOOGLE_CLIENT_SECRET="your_actual_secret"
$env:MARKETSTACK_API_KEY="your_actual_key"
```

#### Windows (Command Prompt)
```cmd
set GOOGLE_CLIENT_ID=your_actual_id
set GOOGLE_CLIENT_SECRET=your_actual_secret
set MARKETSTACK_API_KEY=your_actual_key
```

#### Linux/macOS (Bash)
```bash
export GOOGLE_CLIENT_ID="your_actual_id"
export GOOGLE_CLIENT_SECRET="your_actual_secret"
export MARKETSTACK_API_KEY="your_actual_key"
```

#### Linux/macOS (Permanent in ~/.bashrc or ~/.zshrc)
```bash
echo 'export GOOGLE_CLIENT_ID="your_actual_id"' >> ~/.bashrc
echo 'export GOOGLE_CLIENT_SECRET="your_actual_secret"' >> ~/.bashrc
echo 'export MARKETSTACK_API_KEY="your_actual_key"' >> ~/.bashrc
source ~/.bashrc
```

### 3. Build and Run
```bash
# Build the project (includes frontend automatically)
mvn clean package

# Run the application
java -jar target/portfolio-manager.jar
```

## Production Deployment (Railway)

This project is configured for one-click deployment to Railway. The `nixpacks.toml` at the root handles the unified build.

### Required Variables
Set these in your Railway dashboard:
- `MARKETSTACK_API_KEY`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

### Optional Database (PostgreSQL)
If using a Railway PostgreSQL service, add:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_JPA_PLATFORM=org.hibernate.dialect.PostgreSQLDialect`
- `SPRING_DATASOURCE_DRIVER=org.postgresql.Driver`

## Obtaining Credentials

### Google OAuth Credentials
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth 2.0 credentials (Desktop Application)
5. Copy Client ID and Client Secret

### MarketStack API Key
1. Sign up at [MarketStack](https://marketstack.com/)
2. Get your free API key from the dashboard
3. Copy the API key

## Security Notes

- **Never commit** `src/main/resources/application.properties` with real credentials
- Always use `application.properties.example` as a template
- Use environment variables for all sensitive data
- Add environment variables to your CI/CD pipeline securely
