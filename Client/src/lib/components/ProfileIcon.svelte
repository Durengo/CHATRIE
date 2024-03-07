<script>
	import { isLoggedIn } from '$lib/stores.js';
	import avatar from '$lib/images/profile.svg';
</script>

{#if $isLoggedIn}
	<img src={avatar} alt="Profile" class="profile-icon" />
{:else}
	<div class="profile-icon placeholder">Not Logged In</div>
{/if}

<style>
	.profile-icon {
		width: 3em;
		height: 3em;
		border-radius: 50%;
		background-color: #ddd;
	}

	.placeholder {
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 0.75em;
		color: #666;
	}
</style>
