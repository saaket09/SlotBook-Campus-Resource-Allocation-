/**
 * SlotBook — API wrapper functions (fetch)
 * Central place for all backend API calls.
 */

// ─── Microservices Endpoints ────────────────────────────────
// User Service     : Port 8081
// Resource Service : Port 8082
// Booking Service  : Port 8083
const USER_API     = "http://localhost:8081/api/users";
const RESOURCE_API = "http://localhost:8082/api/resources";
const BOOKING_API  = "http://localhost:8083/api/bookings";

// ─── Resources (Port 8082) ──────────────────────────────────

async function getResources(category = "") {
    const url = category
        ? `${RESOURCE_API}?category=${encodeURIComponent(category)}`
        : `${RESOURCE_API}`;
    const res = await fetch(url);
    if (!res.ok) throw new Error("Failed to fetch resources");
    return res.json();
}

async function getAvailability(resourceId, date) {
    const res = await fetch(`${RESOURCE_API}/${resourceId}/availability?date=${date}`);
    if (!res.ok) throw new Error("Failed to fetch availability");
    return res.json();
}

async function addResource(resourceData) {
    const res = await fetch(`${RESOURCE_API}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(resourceData)
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to add resource");
    }
    return res.json();
}

// ─── Bookings (Port 8083) ───────────────────────────────────

async function createBooking(bookingData) {
    const res = await fetch(`${BOOKING_API}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingData)
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to create booking");
    }
    return res.json();
}

async function getUserBookings(userId) {
    const res = await fetch(`${BOOKING_API}/user/${userId}`);
    if (!res.ok) throw new Error("Failed to fetch bookings");
    return res.json();
}

async function cancelBooking(bookingId) {
    const res = await fetch(`${BOOKING_API}/${bookingId}`, {
        method: "DELETE"
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Failed to cancel booking");
    }
    return res.json();
}

// ─── Users (Port 8081) ──────────────────────────────────────

async function registerUser(userData) {
    const res = await fetch(`${USER_API}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData)
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Registration failed");
    }
    return res.json();
}

async function loginUser(email, password) {
    const res = await fetch(`${USER_API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.message || "Login failed");
    }
    return res.json();
}

// ─── Auth Helpers ───────────────────────────────────────────

function isLoggedIn() {
    return localStorage.getItem('userId') !== null;
}

function getCurrentUserId() {
    return localStorage.getItem('userId');
}

function getCurrentUserName() {
    return localStorage.getItem('userName');
}

function logout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    window.location.href = 'login.html';
}

/**
 * Update nav bar to show logged-in user or login button.
 */
function updateNavAuth() {
    const navUser = document.getElementById('nav-user');
    if (!navUser) return;

    if (isLoggedIn()) {
        const name = getCurrentUserName();
        navUser.innerHTML = `
            <div class="nav-user-info">
                <span class="nav-user-avatar">${name.charAt(0).toUpperCase()}</span>
                <span class="nav-user-name">${name}</span>
                <button class="btn btn-outline btn-sm" onclick="logout()" id="nav-logout-btn">Logout</button>
            </div>
        `;
    } else {
        navUser.innerHTML = `<a href="login.html" class="btn btn-outline btn-sm" id="nav-login-btn">Login</a>`;
    }
}

// ─── Toast Notifications ────────────────────────────────────

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
    toast.innerHTML = `
        <span class="toast-icon">${icon}</span>
        <span class="toast-message">${message}</span>
    `;
    container.appendChild(toast);

    // Trigger animation
    requestAnimationFrame(() => toast.classList.add('toast-show'));

    // Auto-remove after 4s
    setTimeout(() => {
        toast.classList.remove('toast-show');
        toast.addEventListener('transitionend', () => toast.remove());
    }, 4000);
}
