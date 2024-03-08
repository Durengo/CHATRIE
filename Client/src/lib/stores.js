import { writable } from 'svelte/store';

// Helper function to check if we're in the browser
function isBrowser() {
	return typeof window !== 'undefined';
}

// Only interact with sessionStorage if we're in the browser
let storedToken = null;
if (isBrowser()) {
	storedToken = sessionStorage.getItem('authToken');
}

let storedUsername = null;
if (isBrowser()) {
	storedUsername = sessionStorage.getItem('loginUsername');
}
export const loginUsername = writable(storedUsername);

let storedLobbyId = null;
if (isBrowser()) {
	storedLobbyId = sessionStorage.getItem('currentLobbyId');
}
export const currentLobbyId = writable(storedLobbyId);

let storedSendToUsername = null;
if (isBrowser()) {
	storedSendToUsername = sessionStorage.getItem('currentSendToUsername');
}
export const currentSendToUsername = writable(storedSendToUsername);

export const isLoggedIn = writable(storedToken !== null);
export const authToken = writable(storedToken);

// Clear sessionStorage on logout
export function logout() {
	sessionStorage.removeItem('authToken');
	authToken.set(null);
	isLoggedIn.set(false);
	sessionStorage.removeItem('loginUsername');
	sessionStorage.removeItem('currentLobbyId');
	sessionStorage.removeItem('currentSendToUsername');

	window.location.href = '/';
}

export const apiBaseUrl = writable('http://localhost:8090');
