<script>
	import { onMount } from 'svelte';
	import { redirect } from '@sveltejs/kit';
	import { goto } from '$app/navigation';
	import { verifyJWT } from '$lib/scripts/authUtils.js';
	import { loginUsername, authToken } from '$lib/stores.js';

	let username = loginUsername;
	let token = authToken;
    let isValid = false;

	onMount(async () => {
		try {
			// Subscribe to the stores on mount
			const unsubscribeUsername = loginUsername.subscribe((value) => {
				username = value;
			});

			const unsubscribeToken = authToken.subscribe((value) => {
				token = value;
			});
			``;

			// console.log('username:', username);
			// console.log('token:', token);

			isValid = await verifyJWT(username, token);

			console.log('valid: ', isValid);

			if (!isValid) {
				goto('/login', { replaceState: true });
			}
		} catch (error) {
			console.error('Error verifying JWT:', error);
		}
	});

	// // Cleanup subscriptions on component destroy
	// onDestroy(() => {
	//     unsubscribeUsername();
	//     unsubscribeToken();
	// });
</script>

<!-- <script>
    // import { onMount } from 'svelte';
    // import { goto } from '$app/navigation';

    // let chatRooms = [];
    // let selectedLobbyId = null;

    // onMount(async () => {
    //     try {
    //         chatRooms = await fetchChatRooms(); 
    //     } catch (error) {
    //         console.error(error);
    //     }
    // });

    // function handleLobbySelect(lobbyId) {
    //     selectedLobbyId = lobbyId;
    //     goto(`/chat/${lobbyId}`);
    // }
</script> -->

{#if isValid}
	<div>
		<h2>Available Chatrooms</h2>
		<ul>
			<!-- {#each chatRooms as room}
            <li on:click={() => handleLobbySelect(room.lobby_id)}>
                {room.lobby_id} - {room.name}
            </li>
        {/each} -->
		</ul>
	</div>
{/if}
