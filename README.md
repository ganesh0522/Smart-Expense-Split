# 💸 Smart Expense Splitter

A full-stack web application to split expenses among groups (like Splitwise), built with **Spring Boot + React**.

🔗 **Live Demo**: <add link>

---

## ✨ Features

* 👥 Create groups & manage members
* 💸 Add expenses with:

  * Equal split
  * Custom split
  * Percentage split
* ⚖️ Real-time balance tracking (per group)
* 🔁 **Optimized settlements** (minimum transactions)
* 🧾 Activity timeline (audit log)
* 🔐 JWT authentication (secure APIs)

---

## 🧠 How It Works (Core Logic)

* **Incremental balances**: Instead of recalculating all debts, pairwise balances are updated on each expense.
* **Netting logic**: Opposite debts cancel out automatically (no duplicates).
* **Settlement optimization**: Converts balances into minimal transactions using a greedy matching of debtors and creditors.

---

## 🏗️ Architecture

```text
Frontend (React + Vite)
        │
        ▼
REST APIs (Spring Boot)
        │
        ▼
MySQL Database
```

* **Controller → Service → Repository** layered design
* **JWT Filter** for authentication
* **Transactional services** for consistency

---

## 🛠️ Tech Stack

**Backend**

* Java, Spring Boot
* Spring Security (JWT)
* JPA / Hibernate
* MySQL

**Frontend**

* React (Hooks)
* Vite
* Tailwind CSS
* Axios

---

## ▶️ Run Locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## 📸 Screenshots

> Add your images in `/screenshots` and link them here.

* **Dashboard**
  ![Dashboard](screenshots/dashboard.png)

* **Groups**
  ![Groups](screenshots/groups.png)

* **Group Details (Expenses + Balances)**
  ![Group Details](screenshots/group-details.png)

* **Activity Timeline**
  ![Activity](screenshots/activity.png)

---

## 🔐 API Highlights

* `POST /api/auth/register`
* `POST /api/auth/login`
* `GET /api/groups`
* `POST /api/expenses`
* `GET /api/balances/group/{id}`
* `GET /api/settlements/{id}`

---

## 📈 Future Improvements

* 🔔 Notifications
* 🔄 Real-time updates (WebSockets)
* 📊 Advanced analytics (per-group insights)
* 🧾 Expense editing & deletion history

---

## 👨‍💻 Author

**Ganesh**
GitHub: https://github.com/ganesh0522
