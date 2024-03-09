<script>
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import { redirect } from '@sveltejs/kit';
	import { goto } from '$app/navigation';
	import { verifyJWT } from '$lib/scripts/authUtils.js';
	import { loginUsername, authToken, currentLobbyId, currentSendToUsername } from '$lib/stores.js';
	import { fetchUsers, createLobby, fetchLobbies } from '$lib/scripts/lobbies.js';
	import { checkSendToWho } from '$lib/scripts/utils.js';
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
				// TODO: Sort the lobbies by most recent message
				chatRooms = await fetchLobbies(username, token);
				users = await fetchUsers(token);
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
	// // Cleanup subscriptions on component destroy
	// onDestroy(() => {
	//     unsubscribeUsername();
	//     unsubscribeToken();
	// });
</script>

{#if isValid}
	<div>
		<h2>Available Chatrooms</h2>
		<ul>
			{#each chatRooms as room}
				<li>
					<button
						on:click={() => {
							console.log('room:', room);
							navigateToChat(room.lobbyId, room.user1Nickname, room.user2Nickname);
						}}
					>
						{room.lobbyId} - {room.user1Nickname} - {room.user2Nickname}
					</button>
				</li>
			{/each}
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
{/if}
