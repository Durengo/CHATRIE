<script>
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import { redirect } from '@sveltejs/kit';
	import { goto } from '$app/navigation';
	import { verifyJWT } from '$lib/scripts/authUtils.js';
	import { loginUsername, authToken, currentLobbyId, currentSendToUsername } from '$lib/stores.js';
	import {
		sortLobbiesByLatestMessage,
		fetchUsers,
		createLobby,
		fetchLobbies
	} from '$lib/scripts/lobbies.js';
	import { formatTimestamp, checkSendToWho } from '$lib/scripts/utils.js';
	import { tick } from 'svelte';

	let username = loginUsername;
	let token = authToken;
	let isValid = false;
	let lobbyId = currentLobbyId;
	let sendToUsername = currentSendToUsername;
	let chatRooms = [];
	// let { id } = useParams();
	// let history = get(chatHistory);
	export const selectedChatId = writable(null);
	let selectedUser = null;
	let users = [];
	let isLoading = true;

	onMount(async () => {
		// Subscribe to the stores on mount
		const unsubscribeUsername = loginUsername.subscribe((value) => {
			username = value;
		});

		const unsubscribeToken = authToken.subscribe((value) => {
			token = value;
		});

		const unsubscribeLobbyId = currentLobbyId.subscribe((value) => {
			lobbyId = value;
		});

		const unsubscribeSendToUsername = currentSendToUsername.subscribe((value) => {
			sendToUsername = value;
		});
		try {
			console.log('username:', username);
			console.log('token:', token);

			isValid = await verifyJWT(username, token);

			console.log('valid: ', isValid);

			if (isValid) {
				// chatRooms = await fetchLobbies(username, token);
				const fetchedLobbies = await fetchLobbies(username, token);
				// Use the sorting system and get more data
				chatRooms = await sortLobbiesByLatestMessage(username, fetchedLobbies, token);
				users = await fetchUsers(token);

				console.log('chatRooms:', chatRooms);

				isLoading = false;
				// history = await fetchChatHistory(username, token);
			} else {
				goto('/login', { replaceState: true });
			}
		} catch (error) {
			console.error('Error verifying JWT:', error);
		}
		// Clean up subscriptions
		unsubscribeUsername();
		unsubscribeToken();
		unsubscribeLobbyId();
		unsubscribeSendToUsername();
	});

	const navigateToChat = async (lobbyId, user1Nickname, user2Nickname) => {
		// Resolve nickname
		let sendToUser = checkSendToWho(username, user1Nickname, user2Nickname);

		sessionStorage.setItem('currentLobbyId', lobbyId);
		sessionStorage.setItem('currentSendToUsername', sendToUser);

		console.log('Navigating to chat:', lobbyId);
		console.log('Chat between:', username, 'and', sendToUser);

		goto(`/chat/${lobbyId}`);
	};

	const createOrOpenChat = async () => {
		if (selectedUser) {
			const lobby = await createLobby(username, selectedUser, token);
			navigateToChat(lobby.lobbyId, username, selectedUser);
		}
	};
</script>

{#if isLoading}
	<p>Loading chat history...</p>
{:else if isValid}
	<div>
		<h2>Available Chatrooms</h2>
		<ul>
			{#if Array.isArray(chatRooms)}
				{#each chatRooms as room}
					<ul>
						<button
							on:click={() => {
								console.log('room:', room);
								navigateToChat(room.lobbyId, username, room.withUser);
							}}
						>
							{#if room.lastMessageFrom === 'None'}
								{'Chat with "' + room.withUser + '" No messages yet.'}
							{:else}
								{'Chat with ' +
									room.withUser +
									' Time: ' +
									formatTimestamp(room.timestamp) +
									' Last Message From: ' +
									room.lastMessageFrom +
									' Message: ' +
									room.message}
							{/if}
						</button>
					</ul>
				{/each}
			{:else}
				<p>Invalid chatRooms data.</p>
			{/if}
		</ul>

		<h2>Create or Open Chat</h2>
		<label for="users">Select a user:</label>
		<select bind:value={selectedUser} id="users">
			<option value="" disabled>Select a user</option>
			{#each users as user}
				<!-- Skip the object if it is the same as the user. -->
				{#if user !== username}
					<option value={user}>{user}</option>
				{/if}
			{/each}
		</select>
		<button on:click={createOrOpenChat} disabled={!selectedUser}>Create/Open Chat</button>
	</div>
{:else}
	<h2>Error loading chat history.</h2>
{/if}
