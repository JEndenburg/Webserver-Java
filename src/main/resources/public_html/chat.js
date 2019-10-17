const messagesUrl = "chatmessages.ahtml";
const inputBox = document.getElementById("chat-input-field");
const messageBox = document.getElementById("chat-box");
const sessionId = Math.random() * 10000;
const sessionHash = Math.round(sessionId * 100000).toString(16);
let ip = null;

async function sendMessage()
{
    if(ip === null)
        return;
    let inputValue = escape(inputBox.value);

    let content = null;

    let userId = ip.replace(/\./g, "");
    let colorId = Math.round((Number(userId) + sessionId) % 10);

    let response = await fetch(messagesUrl, { method: "POST", body: ("message=" + inputValue + "&cid=" + colorId + "&ip=" + ip + "&hash=" + sessionHash), headers: { "content-type": "application/x-www-form-urlencoded" } });
    let responseText = await response.text();

    inputBox.value = "";
    buildContents(responseText);
}

async function fetchMessages()
{
    let response = await fetch(messagesUrl);
    let responseText = await response.text();

    buildContents(responseText);
}

function buildContents(responseText)
{
    let oldMessage = messageBox.querySelector(".chat-message");
    messageBox.innerHTML = unescape(responseText);
    let newMessage = messageBox.querySelector(".chat-message");

    if(oldMessage == null || oldMessage.innerHTML != newMessage.innerHTML)
    {
        if(newMessage != null)
            notifyUser(newMessage);
    }
}

function notifyUser(newMessage)
{
    if(!("Notification" in window))
        return; //Not supported
    
    switch(Notification.permission)
    {
        case "granted":
        showNotification(newMessage);
        break;
        case "denied":
        //Do nothing.
        break;
        default:
        Notification.requestPermission();
        break;
    }
}

function showNotification(newMessage)
{
    let authorInfo = newMessage.querySelector(".chat-info");
    let messageAuthorHash = authorInfo.querySelector(".hash").innerText;

    if(messageAuthorHash == sessionHash)
        return;

    let messageAuthor = authorInfo.querySelector(".ip").innerText + ":" + authorInfo.querySelector(".hash").innerText;
    let message = newMessage.querySelector(".message").innerText;

    let notifOptions = 
    {
        body: messageAuthor,
        icon: window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/img/chat_ico.gif"
    };

    console.log(notifOptions.icon);

    new Notification(
        message, notifOptions
    );
}

fetchMessages();
setInterval(fetchMessages, 3000);
fetch("https://api.ipify.org?format=json")
    .catch(e => ip = "0.0.0.0")
    .then(resp => resp.json())
    .then(respJson => ip = respJson.ip)
    .catch(e => console.log(e));