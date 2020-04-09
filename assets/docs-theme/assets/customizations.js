
function createHeaderLogo() {
    var headerLeft = document.getElementById('headerLeft');
    var div = document.createElement('div');
    div.id="tp-custom-header-content";
    var img = document.createElement('img');
    img.id = 'tp-logo';
    img.src = '../../../../assets/images/TP_logo_black.svg';
    if (window.location.pathname.endsWith('packages.html')) {
        img.src = '../assets/images/TP_logo_black.svg';
    }
    var a = document.createElement('a');
    a.href = 'https://tajnyprojekt.com';
    var span = document.createElement('span');
    span.innerHTML = '<b>tajny</b>projekt'
    a.append(img);
    a.append(span);
    div.append(a);
    headerLeft.append(div);
}

function createText() {
    var headerRight = document.getElementById('headerRight');
    var div = document.createElement('div');
    div.id="tp-custom-header-content-right";
    div.innerHTML = 'Docs: TpAnimation library for Processing';
    headerRight.append(div);
}

$(document).ready(function() {
    createHeaderLogo();
    createText();
});