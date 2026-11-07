/**
 * SlotBook — Resources page logic
 * Renders the resource card grid and handles category filtering.
 */

const resourceGrid = document.getElementById('resource-grid');
const emptyState = document.getElementById('empty-state');
const loadingSpinner = document.getElementById('loading-spinner');
const filterBtns = document.querySelectorAll('.filter-btn');

let currentCategory = '';

// Category icon and color mapping
const categoryMeta = {
    HALL:      { icon: '🏛️', label: 'Hall' },
    SPORTS:    { icon: '🏸', label: 'Sports' },
    LAB:       { icon: '🔬', label: 'Lab' },
    EQUIPMENT: { icon: '🎛️', label: 'Equipment' }
};

/**
 * Fetch and render resources.
 */
async function loadResources(category = '', retryCount = 0) {
    loadingSpinner.classList.remove('hidden');
    resourceGrid.innerHTML = '';
    emptyState.classList.add('hidden');

    try {
        const resources = await getResources(category);
        loadingSpinner.classList.add('hidden');

        if (resources.length === 0) {
            emptyState.classList.remove('hidden');
            return;
        }

        resources.forEach((resource, index) => {
            const card = createResourceCard(resource, index);
            resourceGrid.appendChild(card);
        });
    } catch (err) {
        // If initial attempt fails, retry smoothly while backend starts up
        if (retryCount < 3) {
            console.log(`Backend warming up, retrying (${retryCount + 1}/3)...`);
            setTimeout(() => loadResources(category, retryCount + 1), 2000);
            return;
        }
        loadingSpinner.classList.add('hidden');
        showToast('Failed to load resources: ' + err.message, 'error');
    }
}

/**
 * Create a resource card element.
 */
function createResourceCard(resource, index) {
    const meta = categoryMeta[resource.category] || { icon: '📦', label: resource.category };
    const card = document.createElement('div');
    card.className = 'resource-card glass-card';
    card.style.animationDelay = `${index * 0.08}s`;

    card.innerHTML = `
        <div class="card-header">
            <span class="category-badge category-${resource.category.toLowerCase()}">${meta.icon} ${meta.label}</span>
            <span class="card-capacity">👥 ${resource.capacity}</span>
        </div>
        <h3 class="card-title">${resource.name}</h3>
        <p class="card-location">📍 ${resource.location}</p>
        <a href="booking.html?resourceId=${resource.resourceId}" class="btn btn-primary btn-full card-btn" id="view-slots-${resource.resourceId}">
            View Slots →
        </a>
    `;

    return card;
}

/**
 * Handle filter button clicks.
 */
filterBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        // Update active state
        filterBtns.forEach(b => b.classList.remove('active'));
        btn.classList.add('active');

        // Load filtered resources
        currentCategory = btn.dataset.category;
        loadResources(currentCategory);
    });
});

// ─── Init ───────────────────────────────────────────────────
updateNavAuth();
loadResources();
