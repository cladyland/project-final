window.onload = function() {
    setupMenuItems();
    chooseMenuItem();
};

// make menu-items active after clicking
function setupMenuItems() {
    let menuItems = document.querySelectorAll('.menu-item');
    if (menuItems.length) {
        menuItems.forEach((menuItem) => {
            menuItem.addEventListener('click', (e) => {
                menuItems.forEach((menuItem) => {
                    menuItem.classList.remove('active');
                });
                // e.preventDefault();
                menuItem.classList.add('active');
            });
        });
    }
}

// click on menu-item if it was selected before page refresh
function chooseMenuItem() {
    let hash = document.location.hash;
    if (hash.length > 0) {
        let menuItemId = hash.split('#')[1];
        let menuItem = document.getElementById(menuItemId);
        if (menuItem) {
            menuItem.click();
        }
    }
}

function changeLanguage(locale) {
    let baseUrl = window.location.href
    let localeParam = '?lang='
    if (baseUrl.includes(localeParam)) {
        let index = (baseUrl + localeParam + locale).indexOf(localeParam)
        baseUrl = (baseUrl + localeParam + locale).substring(0, index)
    }
    window.location.href = baseUrl + localeParam + locale
}
