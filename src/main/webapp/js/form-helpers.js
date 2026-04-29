// Shared form sanitization + validation helpers for ProcureGov
(function () {
  'use strict';

  function sanitize(value) {
    if (value == null) return '';
    value = String(value).trim();
    // Remove control characters (except common whitespace) and null bytes
    value = value.replace(/[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]/g, '');
    // Strip HTML tags
    value = value.replace(/<[^>]*>/g, '');
    return value;
  }

  function escapeHTML(str) {
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function showError(form, message) {
    if (!form) return;
    var el = form.querySelector('#clientError');
    // fallback to global element
    if (!el) el = document.getElementById('clientError');
    if (!el) return;
    el.style.display = 'flex';
    el.setAttribute('role', 'alert');
    el.innerHTML = '<span class="material-symbols-outlined">error</span><span>' + escapeHTML(message) + '</span>';
  }

  function clearError(form) {
    if (!form) return;
    var el = form.querySelector('#clientError');
    if (!el) el = document.getElementById('clientError');
    if (!el) return;
    el.style.display = 'none';
    el.innerHTML = '';
  }

  function validateEmail(email) {
    if (!email) return false;
    var re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }

  function validatePassword(password, minLength) {
    if (!password) return false;
    minLength = parseInt(minLength, 10) || 8;
    return password.length >= minLength;
  }

  function attachValidationToForm(form) {
    if (!form) return;

    form.addEventListener('submit', function (evt) {
      // Sanitize all text-like inputs and textareas
      var textFields = form.querySelectorAll('input:not([type="file"]):not([type="checkbox"]):not([type="radio"]):not([type="hidden"]), textarea');
      textFields.forEach(function (el) {
        try {
          el.value = sanitize(el.value);
        } catch (e) {
          // ignore
        }
      });

      // Let browser validate required/pattern/minlength attributes first
      if (!form.checkValidity()) {
        // Use native UI to show which field is invalid where supported
        try { form.reportValidity(); } catch (e) { }
        evt.preventDefault();
        return false;
      }

      // Specific semantic checks: email and password if present
      var emailInput = form.querySelector('input[name="email"], input[type="email"]');
      var passwordInput = form.querySelector('input[name="password"], input[type="password"]');
      var email = emailInput ? emailInput.value : '';
      var password = passwordInput ? passwordInput.value : '';
      var minPasswordLength = form.getAttribute('data-min-password-length') || 8;

      if (emailInput) {
        if (!validateEmail(email)) {
          showError(form, 'Please enter a valid email address (e.g. name@agency.gov.ls).');
          emailInput.focus();
          evt.preventDefault();
          return false;
        }
      }

      if (passwordInput) {
        if (!validatePassword(password, minPasswordLength)) {
          showError(form, 'Password must be at least ' + minPasswordLength + ' characters long.');
          passwordInput.focus();
          evt.preventDefault();
          return false;
        }
      }

      clearError(form);
      return true; // allow submit
    }, false);

    // Clear error when user types
    var inputs = form.querySelectorAll('input');
    inputs.forEach(function (input) {
      input.addEventListener('input', function () {
        clearError(form);
      });
    });
  }

  // Auto-attach to forms marked with data-validate="true"
  document.addEventListener('DOMContentLoaded', function () {
    var forms = document.querySelectorAll('form[data-validate="true"]');
    forms.forEach(function (f) {
      attachValidationToForm(f);
    });
  });

  // Expose helpers for pages that want to use them directly
  window.pgFormHelpers = {
    sanitize: sanitize,
    validateEmail: validateEmail,
    validatePassword: validatePassword,
    attachValidationToForm: attachValidationToForm,
    showError: showError,
    clearError: clearError
  };
})();


