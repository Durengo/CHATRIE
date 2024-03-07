import { apiBaseUrl, authToken, isLoggedIn, loginUsername } from '$lib/stores.js';

export async function fetchChatRooms() {
    const response = await fetch(`${apiBaseUrl}/lobbies`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        // body: JSON.stringify({ username, password }),
        Authorization: `Bearer ${authToken}`
    });

    if (response.ok) {
        return await response.json();
    } else {
        throw new Error("Failed to fetch chatrooms");
    }
 }
 
 export async function fetchChatHistory(lobbyId) {
     // Similar structure to fetchChatRooms, making a request 
     // to an endpoint like '/api/chathistory?lobbyId=...'
 }