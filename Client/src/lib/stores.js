// src/lib/stores.js
import { writable } from 'svelte/store';

export const isLoggedIn = writable(false);
export const user = writable(null);