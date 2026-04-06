/**
 * BloodHub - Blood Management System
 * Main JavaScript file for interactive functionality
 */

// Global variables
let currentUser = null;
let notifications = [];

// DOM Ready
document.addEventListener('DOMContentLoaded', function () {
    initializeApp();
    setupEventListeners();
    loadUserPreferences();

    //  Initialize Select2 safely (if defined)
    if (typeof initializeSelect2 === 'function') {
        initializeSelect2();
    }
});

/**
 * Initialize the application
 */
function initializeApp() {
    console.log('BloodHub application initializing...');

    // Check if user is authenticated
    checkAuthenticationStatus();

    // Initialize tooltips and popovers
    initializeBootstrapComponents();

    // Setup auto-hide for alerts
    setupAlertAutoHide();

    // Setup form validation
    setupFormValidation();
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Navigation toggle for mobile
    const navbarToggler = document.querySelector('.navbar-toggler');
    if (navbarToggler) {
        navbarToggler.addEventListener('click', function () {
            const navbarCollapse = document.querySelector('.navbar-collapse');
            if (navbarCollapse) {
                navbarCollapse.classList.toggle('show');
            }
        });
    }

    // Dropdown menus
    /* const dropdownToggles = document.querySelectorAll('.dropdown-toggle');
     dropdownToggles.forEach(toggle => {
         toggle.addEventListener('click', function(e) {
             e.preventDefault();
             const dropdownMenu = this.nextElementSibling;
             if (dropdownMenu) {
                 dropdownMenu.classList.toggle('show');
             }
         });
     });  */

    // Close dropdowns when clicking outside
    /* document.addEventListener('click', function(e) {
         if (!e.target.matches('.dropdown-toggle')) {
             const dropdowns = document.querySelectorAll('.dropdown-menu');
             dropdowns.forEach(dropdown => {
                 if (dropdown.classList.contains('show')) {
                     dropdown.classList.remove('show');
                 }
             });
         }
     });  */

    // Form submissions
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        if (form.method.toLowerCase() !== 'get') {
            form.addEventListener('submit', handleFormSubmit);
        }
    });

    // Search functionality
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        input.addEventListener('input', debounce(handleSearch, 300));
    });
}

/**
 * Initialize Bootstrap components
 */
function initializeBootstrapComponents() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
}

/**
 * Setup alert auto-hide
 */
function setupAlertAutoHide() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.parentNode) {
                alert.classList.add('fade');
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.remove();
                    }
                }, 150);
            }
        }, 5000);
    });
}



/**
 * Setup form validation
 */
function setupFormValidation() {
    document.querySelectorAll('form[data-validate]').forEach(form => {
        form.addEventListener('submit', function (e) {
            if (!validateForm(this)) {
                e.preventDefault();
                showFormErrors(this);
            }
        });
    });
}

/**
 * Handle form submission
 */
function handleFormSubmit(e) {
    const form = e.target;

    // Don't interfere with GET forms (like search)
    if (form.method.toLowerCase() === 'get') {
        return;
    }

    const submitBtn = form.querySelector('button[type="submit"]');

    if (submitBtn) {
        // Show loading state
        // const originalText = submitBtn.innerHTML;
        // submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
        // submitBtn.disabled = true;

        // Re-enable after a delay (in case of validation errors)
        /* setTimeout(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }, 3000); */
    }
}

/**
 * Validate form
 */
function validateForm(form) {
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            field.classList.add('is-invalid');
        } else {
            field.classList.remove('is-invalid');
        }
    });

    // Email validation
    const emailFields = form.querySelectorAll('input[type="email"]');
    emailFields.forEach(field => {
        if (field.value && !isValidEmail(field.value)) {
            isValid = false;
            field.classList.add('is-invalid');
        }
    });

    return isValid;
}

/**
 * Show form errors
 */
function showFormErrors(form) {
    const invalidFields = form.querySelectorAll('.is-invalid');
    if (invalidFields.length > 0) {
        invalidFields[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
        showNotification('Please fill in all required fields correctly.', 'error');
    }
}

/**
 * Email validation
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

/**
 * Handle search
 */
function handleSearch(e) {
    const query = e.target.value.trim();
    if (query.length >= 2) {
        // Perform search
        performSearch(query);
    }
}

/**
 * Perform search
 */
function performSearch(query) {
    // Implementation for search functionality
    console.log('Searching for:', query);

    // You can implement AJAX search here
    // For now, just log the search query
}

/**
 * Check authentication status
 */
function checkAuthenticationStatus() {
    // Check if user is logged in
    const userDropdown = document.querySelector('.dropdown-toggle');
    if (userDropdown) {
        currentUser = {
            name: userDropdown.querySelector('span')?.textContent || 'User',
            email: userDropdown.getAttribute('data-email') || ''
        };
        console.log('User authenticated:', currentUser.name);
    }
}

/**
 * Load user preferences
 */
function loadUserPreferences() {
    // Load user preferences from localStorage
    const theme = localStorage.getItem('bloodhub-theme') || 'light';
    const language = localStorage.getItem('bloodhub-language') || 'en';

    applyTheme(theme);
    applyLanguage(language);
}

/**
 * Apply theme
 */
function applyTheme(theme) {
    document.body.setAttribute('data-theme', theme);
    localStorage.setItem('bloodhub-theme', theme);
}

/**
 * Apply language
 */
function applyLanguage(language) {
    document.documentElement.setAttribute('lang', language);
    localStorage.setItem('bloodhub-language', language);
}

/**
 * Show notification
 */
function showNotification(message, type = 'info', duration = 5000) {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';

    notification.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(notification);

    // Auto-hide after duration
    setTimeout(() => {
        if (notification.parentNode) {
            notification.remove();
        }
    }, duration);

    // Store notification
    notifications.push({
        id: Date.now(),
        message,
        type,
        timestamp: new Date()
    });
}

/**
 * Toggle theme
 */
function toggleTheme() {
    const currentTheme = document.body.getAttribute('data-theme') || 'light';
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    applyTheme(newTheme);

    showNotification(`Theme changed to ${newTheme} mode`, 'success');
}

/**
 * Share content
 */
function shareContent(title, text, url) {
    if (navigator.share) {
        navigator.share({
            title: title,
            text: text,
            url: url
        }).then(() => {
            showNotification('Content shared successfully!', 'success');
        }).catch((error) => {
            console.log('Error sharing:', error);
            showNotification('Failed to share content', 'error');
        });
    } else {
        // Fallback for browsers that don't support Web Share API
        copyToClipboard(url);
        showNotification('URL copied to clipboard!', 'success');
    }
}

/**
 * Copy to clipboard
 */
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text);
    } else {
        // Fallback for older browsers
        const textArea = document.createElement('textarea');
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
    }
}

/**
 * Debounce function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Format date
 */
function formatDate(date, format = 'DD/MM/YYYY') {
    const d = new Date(date);
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();

    return format
        .replace('DD', day)
        .replace('MM', month)
        .replace('YYYY', year);
}

/**
 * Format phone number
 */
function formatPhoneNumber(phone) {
    // Remove all non-digits
    const cleaned = phone.replace(/\D/g, '');

    // Format based on length
    if (cleaned.length === 11 && cleaned.startsWith('01')) {
        return cleaned.replace(/(\d{2})(\d{3})(\d{3})(\d{3})/, '$1 $2 $3 $4');
    } else if (cleaned.length === 10 && cleaned.startsWith('1')) {
        return cleaned.replace(/(\d{1})(\d{3})(\d{3})(\d{3})/, '0$1 $2 $3 $4');
    }

    return phone;
}

/**
 * Validate phone number
 */
function validatePhoneNumber(phone) {
    const phoneRegex = /^(?:\+880|880|0)?1[3-9]\d{8}$/;
    return phoneRegex.test(phone.replace(/\s/g, ''));
}

/**
 * Show loading spinner
 */
function showLoading(element) {
    if (element) {
        element.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Loading...';
        element.disabled = true;
    }
}

/**
 * Hide loading spinner
 */
function hideLoading(element, originalText) {
    if (element) {
        element.innerHTML = originalText;
        element.disabled = false;
    }
}

/**
 * Handle API errors
 */
function handleApiError(error, userMessage = 'An error occurred. Please try again.') {
    console.error('API Error:', error);

    if (error.response) {
        // Server responded with error status
        const status = error.response.status;
        let message = userMessage;

        switch (status) {
            case 400:
                message = 'Invalid request. Please check your input.';
                break;
            case 401:
                message = 'Please log in to continue.';
                break;
            case 403:
                message = 'You do not have permission to perform this action.';
                break;
            case 404:
                message = 'The requested resource was not found.';
                break;
            case 500:
                message = 'Server error. Please try again later.';
                break;
        }

        showNotification(message, 'error');
    } else if (error.request) {
        // Network error
        showNotification('Network error. Please check your connection.', 'error');
    } else {
        // Other error
        showNotification(userMessage, 'error');
    }
}

/**
 * Export functions to global scope
 */
window.BloodHub = {
    showNotification,
    toggleTheme,
    shareContent,
    copyToClipboard,
    formatDate,
    formatPhoneNumber,
    validatePhoneNumber,
    showLoading,
    hideLoading,
    handleApiError
};

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = window.BloodHub;
}
