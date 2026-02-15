window.onload = function() {
  // Create the toggle button
  var toggleButton = document.createElement('button');
  toggleButton.id = 'swagger-ui-theme-toggle';
  toggleButton.innerText = 'Dark Mode'; 
  toggleButton.style.cssText = 'margin-left: 20px; padding: 5px 10px; background: transparent; color: white; border: 1px solid white; border-radius: 4px; cursor: pointer;';

  // Find the topbar wrapper to insert the button
  var topbarWrapper = document.querySelector('.swagger-ui .topbar .wrapper');
  if (topbarWrapper) {
      topbarWrapper.appendChild(toggleButton);
  }

  // Check for saved user preference
  var savedTheme = localStorage.getItem('swagger-ui-theme');
  if (savedTheme === 'dark') {
      document.body.classList.add('dark-mode');
      toggleButton.innerText = 'Light Mode';
  }

  // Toggle function
  toggleButton.onclick = function() {
      document.body.classList.toggle('dark-mode');
      var isDarkMode = document.body.classList.contains('dark-mode');
      localStorage.setItem('swagger-ui-theme', isDarkMode ? 'dark' : 'light');
      toggleButton.innerText = isDarkMode ? 'Light Mode' : 'Dark Mode';
  };
};
