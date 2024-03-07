<script>
	import { onMount } from 'svelte';
	import { loginUsername, authToken, currentLobbyId } from '$lib/stores.js';
	import { fetchChatHistory } from '../../../lib/lobbies.js';
	import { formatTimestamp } from '$lib/scripts/utils.js';

	let token = authToken;
	let chatHistory = [];
	let lobbyId = currentLobbyId;

	onMount(async () => {
		try {
			const unsubscribeToken = authToken.subscribe((value) => {
				token = value;
			});

			const unsubscribeLobbyId = currentLobbyId.subscribe((value) => {
				lobbyId = value;
			});

			console.log('lobbyId:', lobbyId);
			console.log('token:', token);

			chatHistory = await fetchChatHistory(lobbyId, token);
		} catch (error) {
			console.error('Error fetching chat history:', error);
			// Handle the error appropriately
		}
	});
</script>

<h1>Chat Room {lobbyId}</h1>

{#if chatHistory}
	<ul>
		{#each chatHistory as message}
			<li>
				{formatTimestamp(message.lobbyChatKey.timestamp)} - {message.sentBy}: {message.message}
			</li>
		{/each}
	</ul>
{:else}
	<p>Loading chat history...</p>
{/if}
