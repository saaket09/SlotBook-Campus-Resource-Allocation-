/**
 * SlotBook — My Bookings page logic
 * Fetches the logged-in user's bookings and renders a table with cancel support.
 */

const bookingsTbody = document.getElementById('bookings-tbody');
const tableWrapper = document.getElementById('bookings-table-wrapper');
const emptyState = document.getElementById('empty-state');
const loginRequired = document.getElementById('login-required');
const loadingSpinner = document.getElementById('loading-spinner');

/**
 * Load and display user's bookings.
 */
async function loadBookings() {
    if (!isLoggedIn()) {
        loadingSpinner.classList.add('hidden');
        tableWrapper.classList.add('hidden');
        loginRequired.classList.remove('hidden');
        return;
    }

    const userId = getCurrentUserId();
    loadingSpinner.classList.remove('hidden');
    tableWrapper.classList.add('hidden');
    emptyState.classList.add('hidden');

    try {
        const bookings = await getUserBookings(userId);
        loadingSpinner.classList.add('hidden');

        if (bookings.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }

        // Sort by date descending
        bookings.sort((a, b) => {
            const dateCompare = b.bookingDate.localeCompare(a.bookingDate);
            if (dateCompare !== 0) return dateCompare;
            return a.startTime.localeCompare(b.startTime);
        });

        renderBookingsTable(bookings);
        tableWrapper.classList.remove('hidden');
    } catch (err) {
        loadingSpinner.classList.add('hidden');
        showToast('Failed to load bookings: ' + err.message, 'error');
    }
}

/**
 * Render bookings table rows.
 */
function renderBookingsTable(bookings) {
    bookingsTbody.innerHTML = bookings.map(booking => {
        const isConfirmed = booking.status === 'CONFIRMED';
        const category = booking.resource?.category || 'N/A';
        const categoryClass = `category-${category.toLowerCase()}`;

        return `
            <tr class="booking-row ${!isConfirmed ? 'row-cancelled' : ''}" id="booking-row-${booking.bookingId}">
                <td>
                    <strong>${booking.resource?.name || 'Unknown'}</strong>
                    <span class="table-location">📍 ${booking.resource?.location || ''}</span>
                </td>
                <td><span class="category-badge ${categoryClass}">${category}</span></td>
                <td>${formatDate(booking.bookingDate)}</td>
                <td>${formatTime(booking.startTime)} — ${formatTime(booking.endTime)}</td>
                <td>
                    <span class="status-badge ${isConfirmed ? 'status-confirmed' : 'status-cancelled'}">
                        ${booking.status}
                    </span>
                </td>
                <td>
                    ${isConfirmed
                        ? `<button class="btn btn-danger btn-sm" onclick="handleCancel(${booking.bookingId})" id="cancel-btn-${booking.bookingId}">Cancel</button>`
                        : `<span class="text-muted">—</span>`
                    }
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Cancel a booking.
 */
async function handleCancel(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;

    try {
        await cancelBooking(bookingId);
        showToast('Booking cancelled successfully.', 'success');
        loadBookings(); // Refresh
    } catch (err) {
        showToast('Failed to cancel: ' + err.message, 'error');
    }
}

// ─── Helpers ────────────────────────────────────────────────

function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
        month: 'short', day: 'numeric', year: 'numeric'
    });
}

function formatTime(timeStr) {
    // timeStr format: "HH:mm:ss" or "HH:mm"
    const parts = timeStr.split(':');
    let hour = parseInt(parts[0]);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    if (hour === 0) hour = 12;
    else if (hour > 12) hour -= 12;
    return `${hour}:${parts[1]} ${ampm}`;
}

// ─── Init ───────────────────────────────────────────────────
updateNavAuth();
loadBookings();
