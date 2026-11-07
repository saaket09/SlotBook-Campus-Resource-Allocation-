/**
 * SlotBook — Booking page logic
 * Shows a day's timeline for a selected resource.
 * Booked slots are greyed out; free slots are clickable.
 */

// ─── DOM Refs ───────────────────────────────────────────────
const timeline = document.getElementById('timeline');
const dateInput = document.getElementById('booking-date');
const resourceNameEl = document.getElementById('resource-name');
const resourceCategoryEl = document.getElementById('resource-category');
const resourceLocationEl = document.getElementById('resource-location');
const resourceCapacityEl = document.getElementById('resource-capacity');
const confirmPanel = document.getElementById('booking-confirm');
const confirmResourceEl = document.getElementById('confirm-resource');
const confirmDateEl = document.getElementById('confirm-date');
const confirmTimeEl = document.getElementById('confirm-time');
const confirmBtn = document.getElementById('confirm-booking-btn');
const cancelSelectionBtn = document.getElementById('cancel-selection-btn');
const loadingSpinner = document.getElementById('loading-spinner');

// ─── State ──────────────────────────────────────────────────
const urlParams = new URLSearchParams(window.location.search);
const resourceId = urlParams.get('resourceId');
let selectedStart = null;
let selectedEnd = null;
let resourceData = null;

// Timeline hours: 8 AM to 9 PM (13 one-hour slots)
const START_HOUR = 8;
const END_HOUR = 21;

// ─── Init ───────────────────────────────────────────────────

if (!resourceId) {
    showToast('No resource selected. Redirecting...', 'error');
    setTimeout(() => window.location.href = 'index.html', 1500);
}

// Set default date to today
const today = new Date();
const todayStr = today.toISOString().split('T')[0];
dateInput.value = todayStr;
dateInput.min = todayStr;

// Date change handler
dateInput.addEventListener('change', () => {
    clearSelection();
    loadAvailability();
});

/**
 * Load resource info and availability.
 */
async function loadAvailability() {
    const date = dateInput.value;
    if (!date || !resourceId) return;

    loadingSpinner.classList.remove('hidden');
    timeline.innerHTML = '';

    try {
        const availability = await getAvailability(resourceId, date);
        resourceData = availability;

        // Update resource info header
        resourceNameEl.textContent = availability.resourceName;
        resourceLocationEl.textContent = `📍 Loading...`;
        resourceCapacityEl.textContent = '';

        // Try loading full resource details
        try {
            const resources = await getResources();
            const res = resources.find(r => r.resourceId == resourceId);
            if (res) {
                resourceCategoryEl.textContent = res.category;
                resourceCategoryEl.className = `category-badge category-${res.category.toLowerCase()}`;
                resourceLocationEl.textContent = `📍 ${res.location}`;
                resourceCapacityEl.textContent = `👥 Capacity: ${res.capacity}`;
            }
        } catch (e) { /* Non-critical */ }

        renderTimeline(availability.bookedSlots);
        loadingSpinner.classList.add('hidden');
    } catch (err) {
        loadingSpinner.classList.add('hidden');
        showToast('Failed to load availability: ' + err.message, 'error');
    }
}

/**
 * Render the hourly timeline.
 */
function renderTimeline(bookedSlots) {
    timeline.innerHTML = '';

    for (let hour = START_HOUR; hour < END_HOUR; hour++) {
        const slotStart = `${String(hour).padStart(2, '0')}:00:00`;
        const slotEnd = `${String(hour + 1).padStart(2, '0')}:00:00`;

        const isBooked = bookedSlots.some(b => {
            return b.startTime < slotEnd && b.endTime > slotStart;
        });

        const bookedBy = isBooked
            ? bookedSlots.find(b => b.startTime < slotEnd && b.endTime > slotStart)?.bookedBy
            : null;

        const slot = document.createElement('div');
        slot.className = `timeline-slot ${isBooked ? 'slot-booked' : 'slot-free'}`;
        slot.dataset.hour = hour;

        const timeLabel = formatHour(hour);
        const endLabel = formatHour(hour + 1);

        slot.innerHTML = `
            <div class="slot-time">
                <span class="slot-start">${timeLabel}</span>
                <span class="slot-dash">—</span>
                <span class="slot-end">${endLabel}</span>
            </div>
            <div class="slot-status">
                ${isBooked
                    ? `<span class="slot-booked-label">🔒 Booked${bookedBy ? ` by ${bookedBy}` : ''}</span>`
                    : `<span class="slot-free-label">Available</span>`
                }
            </div>
        `;

        if (!isBooked) {
            slot.addEventListener('click', () => selectSlot(hour, slot));
        }

        timeline.appendChild(slot);
    }
}

/**
 * Handle slot selection.
 */
function selectSlot(hour, slotElement) {
    // Clear previous selection
    document.querySelectorAll('.timeline-slot.slot-selected').forEach(s =>
        s.classList.remove('slot-selected')
    );

    slotElement.classList.add('slot-selected');
    selectedStart = `${String(hour).padStart(2, '0')}:00`;
    selectedEnd = `${String(hour + 1).padStart(2, '0')}:00`;

    // Show confirm panel
    confirmResourceEl.textContent = resourceData.resourceName;
    confirmDateEl.textContent = formatDate(dateInput.value);
    confirmTimeEl.textContent = `${formatHour(hour)} — ${formatHour(hour + 1)}`;
    confirmPanel.classList.remove('hidden');
    confirmPanel.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

function clearSelection() {
    selectedStart = null;
    selectedEnd = null;
    confirmPanel.classList.add('hidden');
    document.querySelectorAll('.timeline-slot.slot-selected').forEach(s =>
        s.classList.remove('slot-selected')
    );
}

cancelSelectionBtn.addEventListener('click', clearSelection);

/**
 * Confirm booking.
 */
confirmBtn.addEventListener('click', async () => {
    if (!isLoggedIn()) {
        showToast('Please login to book a slot.', 'error');
        setTimeout(() => window.location.href = 'login.html', 1500);
        return;
    }

    if (!selectedStart || !selectedEnd) return;

    const bookingData = {
        resourceId: parseInt(resourceId),
        userId: parseInt(getCurrentUserId()),
        bookingDate: dateInput.value,
        startTime: selectedStart,
        endTime: selectedEnd
    };

    try {
        confirmBtn.querySelector('.btn-text').classList.add('hidden');
        confirmBtn.querySelector('.btn-loader').classList.remove('hidden');
        confirmBtn.disabled = true;

        await createBooking(bookingData);
        showToast('Booking confirmed! 🎉', 'success');
        clearSelection();
        loadAvailability(); // Refresh the timeline
    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        confirmBtn.querySelector('.btn-text').classList.remove('hidden');
        confirmBtn.querySelector('.btn-loader').classList.add('hidden');
        confirmBtn.disabled = false;
    }
});

// ─── Helpers ────────────────────────────────────────────────

function formatHour(hour) {
    if (hour === 0 || hour === 24) return '12 AM';
    if (hour === 12) return '12 PM';
    if (hour < 12) return `${hour} AM`;
    return `${hour - 12} PM`;
}

function formatDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', {
        weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    });
}

// ─── Start ──────────────────────────────────────────────────
updateNavAuth();
loadAvailability();
