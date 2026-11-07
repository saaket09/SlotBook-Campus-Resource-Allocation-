# SlotBook — Campus Resource Booking System
### Project spec & scaffold (hand this directly to your Antigravity agent)

A system where students/staff book shared campus resources (seminar halls, gym slots, sports equipment, labs) without double-booking. Core challenge: conflict-free time-slot scheduling.

---

## 1. Tech Stack
- **Frontend:** HTML5, CSS3, Vanilla JavaScript (fetch API, DOM manipulation)
- **Backend:** Spring Boot (REST APIs, 3-layer architecture: Controller → Service → Repository)
- **Database:** MySQL/PostgreSQL via Spring Data JPA + JDBC
- **Build tool:** Maven

---

## 2. Folder Structure

```
slotbook/
├── backend/
│   ├── src/main/java/com/slotbook/
│   │   ├── SlotbookApplication.java
│   │   ├── controller/
│   │   │   ├── ResourceController.java
│   │   │   ├── BookingController.java
│   │   │   └── UserController.java
│   │   ├── service/
│   │   │   ├── ResourceService.java
│   │   │   ├── BookingService.java       // conflict-detection logic lives here
│   │   │   └── UserService.java
│   │   ├── repository/
│   │   │   ├── ResourceRepository.java
│   │   │   ├── BookingRepository.java
│   │   │   └── UserRepository.java
│   │   ├── entity/
│   │   │   ├── Resource.java
│   │   │   ├── Booking.java
│   │   │   └── User.java
│   │   └── dto/
│   │       ├── BookingRequestDTO.java
│   │       └── AvailabilityResponseDTO.java
│   └── src/main/resources/
│       ├── application.properties
│       └── schema.sql
└── frontend/
    ├── index.html          // resource grid / home
    ├── login.html
    ├── booking.html        // slot picker for a chosen resource
    ├── my-bookings.html    // user's booking history
    ├── admin.html          // add/manage resources (optional stretch)
    ├── css/
    │   └── style.css
    └── js/
        ├── api.js          // fetch() wrapper functions
        ├── resources.js    // render resource grid, availability status
        ├── booking.js      // slot selection + conflict check
        └── mybookings.js
```

---

## 3. Database Schema (SQL)

```sql
CREATE DATABASE slotbook;
USE slotbook;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'STUDENT'   -- STUDENT or ADMIN
);

CREATE TABLE resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,          -- e.g. "Seminar Hall B", "Badminton Court 1"
    category VARCHAR(50),                -- HALL, SPORTS, LAB, EQUIPMENT
    location VARCHAR(100),
    capacity INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    user_id INT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED',  -- CONFIRMED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- The core query: check overlap before inserting a new booking
-- Two ranges overlap if: existing.start < new.end AND existing.end > new.start
SELECT * FROM bookings
WHERE resource_id = ?
  AND booking_date = ?
  AND status = 'CONFIRMED'
  AND start_time < ?   -- new end_time
  AND end_time > ?;    -- new start_time
```

---

## 4. Core Entities (Spring Boot / JPA)

**Resource.java**
```java
@Entity
public class Resource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;
    private String name;
    private String category;
    private String location;
    private Integer capacity;
    private Boolean isActive = true;
    // getters/setters
}
```

**Booking.java**
```java
@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status = "CONFIRMED";
    private LocalDateTime createdAt = LocalDateTime.now();
    // getters/setters
}
```

---

## 5. Conflict-Detection Logic (the interesting part)

**BookingRepository.java**
```java
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.resource.resourceId = :resourceId " +
           "AND b.bookingDate = :date AND b.status = 'CONFIRMED' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime")
    List<Booking> findConflicts(Long resourceId, LocalDate date,
                                  LocalTime startTime, LocalTime endTime);
}
```

**BookingService.java**
```java
@Service
public class BookingService {
    @Autowired private BookingRepository bookingRepository;

    public Booking createBooking(BookingRequestDTO request) {
        List<Booking> conflicts = bookingRepository.findConflicts(
            request.getResourceId(), request.getBookingDate(),
            request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Slot already booked — choose another time.");
        }
        // build and save new Booking entity
    }
}
```

---

## 6. REST API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/resources` | List all resources (with filters: category, location) |
| GET | `/api/resources/{id}/availability?date=` | Get booked slots for a resource on a date |
| POST | `/api/bookings` | Create a booking (runs conflict check) |
| GET | `/api/bookings/user/{userId}` | Get a user's bookings |
| DELETE | `/api/bookings/{id}` | Cancel a booking |
| POST | `/api/users/register` | Register |
| POST | `/api/users/login` | Login |
| POST | `/api/resources` | (Admin) Add new resource |

---

## 7. Frontend Pages (HTML/CSS/JS)

- **index.html** — grid of resource cards (name, category, location, "View Slots" button), filter bar by category
- **booking.html** — shows a day's timeline for the selected resource; booked slots greyed out, click-to-select a free slot, confirm button calls `POST /api/bookings`
- **my-bookings.html** — table of the logged-in user's bookings with a Cancel button per row
- **admin.html** *(stretch goal)* — form to add new resources

**js/api.js** — central place for all fetch calls:
```javascript
const BASE_URL = "http://localhost:8080/api";

async function getResources(category = "") {
  const res = await fetch(`${BASE_URL}/resources?category=${category}`);
  return res.json();
}

async function createBooking(bookingData) {
  const res = await fetch(`${BASE_URL}/bookings`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(bookingData)
  });
  if (!res.ok) throw new Error((await res.json()).message);
  return res.json();
}
```

---

## 8. Build Order (recommended sequence for the agent)

1. Set up Spring Boot project (Spring Initializr deps: Web, JPA, MySQL Driver) + schema.sql
2. Build entities → repositories → services → controllers, in that order
3. Test all endpoints with Postman/curl before touching frontend
4. Build index.html + resources.js to confirm GET /api/resources renders correctly
5. Build booking.html + booking.js, wire up the conflict-check flow end-to-end
6. Build my-bookings.html for cancel/view history
7. Style everything with CSS last, once functionality works

---

## 9. Stretch Goals (if time permits)
- Simple login/session (even just localStorage of userId, no real auth needed for a course project)
- Email/console notification on booking confirmation
- Admin view to add/deactivate resources
- Calendar-style weekly view instead of single-day slot picker
