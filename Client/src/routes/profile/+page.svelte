<script>
    import { isLoggedIn } from '../../stores.jss';
    import { goto } from '$app/navigation';

    if (!$isLoggedIn) {
        goto('/login'); 
    }
</script>

<h1>Your Profile</h1>
