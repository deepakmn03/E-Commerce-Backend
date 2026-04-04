# API Testing Folder

All API testing resources organized for easy access and team collaboration.

## 📁 Contents

| File | Purpose |
|------|---------|
| **openapi.yaml** | Complete OpenAPI 3.0 API specification |
| **requests.http** | Manual REST Client test requests (50+) |
| **.env.example** | Configuration template |
| **API_TESTING_GUIDE.md** | Comprehensive strategy guide |
| **API_TESTING_QUICK_REFERENCE.md** | Quick reference |
| **API_TESTING_SETUP_COMPLETE.md** | Setup summary |

## 🚀 Quick Start

### 1. Install REST Client Extension
VS Code → Extensions → Search "REST Client" → Install by Huachao Mao

### 2. Setup Environment
```bash
cp .env.example .env
# Edit .env with your values (never commit)
```

### 3. Test Your APIs
- Open `requests.http`
- Click "Send Request" above any test
- View response in side panel

### 4. View API Documentation
- Go to https://editor.swagger.io/
- Paste content from `openapi.yaml`
- Get interactive API docs

## 📚 Documentation

- **API_TESTING_QUICK_REFERENCE.md** - Common tasks and quick reference
- **API_TESTING_GUIDE.md** - Complete strategy and best practices
- **openapi.yaml** - Full API specification

## ✅ Best Practices

✅ Use `{{baseUrl}}` and `{{token}}` variables
✅ Store requests in Git (version control)
✅ Never commit `.env` (use .gitignore)
✅ Keep openapi.yaml updated
✅ Share requests.http with team

---

**Start here:** Read API_TESTING_QUICK_REFERENCE.md
