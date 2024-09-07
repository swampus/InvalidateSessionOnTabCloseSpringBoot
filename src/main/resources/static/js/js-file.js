//random is so-so
if (!sessionStorage.getItem('tabId')) {
    sessionStorage.setItem('tabId', Math.random().toString(36).substr(2, 9));
}

function updateTabCount(count) {
    document.getElementById('tabCount').innerText = `Number of open tabs: ${count}`;
}

const bc = new BroadcastChannel('tabs_channel');
bc.onmessage = (event) => {
    updateTabCount(event.data);
};

function updateTabCountInStorage(increment) {
    let count = parseInt(localStorage.getItem('tabCount') || '0', 10);
    count += increment;
    localStorage.setItem('tabCount', count);
    updateTabCount(count);
    bc.postMessage(count);

    sendTabCountToServer(count);
}

window.addEventListener('load', function() {
    const tabId = sessionStorage.getItem('tabId');
    let count = parseInt(localStorage.getItem('tabCount') || '0', 10);

    if (!sessionStorage.getItem('tabInitialized')) {
        updateTabCountInStorage(1);
        sessionStorage.setItem('tabInitialized', true);
    } else {
        updateTabCount(count);
    }
});

window.addEventListener('beforeunload', function() {
    if (sessionStorage.getItem('tabInitialized')) {
        const tabId = sessionStorage.getItem('tabId');
        sessionStorage.removeItem('tabInitialized');
        sessionStorage.removeItem('tabId');

        let count = parseInt(localStorage.getItem('tabCount'), 10);
        updateTabCountInStorage(-1);
    }
});


// Send the tab count to the server
function sendTabCountToServer(count) {
    fetch('/updateTabCount', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ count: count })
    }).then(response => {
        if (!response.ok) {
            console.error('Failed to update tab count on the server');
        }
    });
}