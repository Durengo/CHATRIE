<script>
	import { onMount } from 'svelte';
	import { writable } from 'svelte/store';
	import { redirect } from '@sveltejs/kit';
	import { goto } from '$app/navigation';
	import { verifyJWT } from '$lib/scripts/authUtils.js';
	import { loginUsername, authToken, currentLobbyId, currentSendToUsername } from '$lib/stores.js';
	import { fetchLobbies } from '$lib/scripts/lobbies.js';
	import { checkSendToWho } from '$lib/scripts/utils.js';

	let username = loginUsername;
	let token = authToken;
	let isValid = false;
	let lobbyId = currentLobbyId;
	let sendToUsername = currentSendToUsername;
	let chatRooms = [];
	// let { id } = useParams();
	// let history = get(chatHistory);
	export const selectedChatId = writable(null);

	onMount(async () => {
		try {
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

			console.log('username:', username);
			console.log('token:', token);

			isValid = await verifyJWT(username, token);

			console.log('valid: ', isValid);

			if (isValid) {
				chatRooms = await fetchLobbies(username, token);
				// history = await fetchChatHistory(username, token);
			} else {
				goto('/login', { replaceState: true });
			}
		} catch (error) {
			console.error('Error verifying JWT:', error);
		}
	});

	const navigateToChat = async (lobbyId, user1Nickname, user2Nickname) => {
		sessionStorage.setItem('currentLobbyId', lobbyId);
		// Resolve nickname
		let sendToUser = checkSendToWho(username, user1Nickname, user2Nickname);
		sessionStorage.setItem('currentSendToUsername', sendToUser);
		console.log('Navigating to chat:', lobbyId);
		console.log('Chat between:', username, 'and', sendToUser);
		goto(`/chat/${lobbyId}`);
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
	</div>
{/if}
