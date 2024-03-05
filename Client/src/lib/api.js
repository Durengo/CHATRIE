//  src/lib/api.js
async function fetchChatRooms() {
    const response = await fetch('/api/chatrooms'); 
    if (response.ok) {
        return await response.json(); // Assuming JSON data
    } else {
        throw new Error("Failed to fetch chatrooms");
    }
 }
 
 async function fetchChatHistory(lobbyId) {
     // Similar structure to fetchChatRooms, making a request 
     // to an endpoint like '/api/chathistory?lobbyId=...'
 }