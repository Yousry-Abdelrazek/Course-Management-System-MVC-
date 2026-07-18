(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        // Mobile sidebar toggle
        var toggle = document.getElementById("sidebarToggle");
        var sidebar = document.querySelector(".cms-sidebar");
        if (toggle && sidebar) {
            toggle.addEventListener("click", function () {
                sidebar.classList.toggle("show");
            });
        }

        // Auto-dismiss flash alerts after 5 seconds
        document.querySelectorAll(".alert-dismissible").forEach(function (alert) {
            window.setTimeout(function () {
                var instance = bootstrap.Alert.getOrCreateInstance(alert);
                instance.close();
            }, 5000);
        });
    });
})();
