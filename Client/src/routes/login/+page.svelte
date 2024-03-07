<script>
    import LoginForm from '$lib/components/LoginForm.svelte';
</script>

<svelte:head>
    <title>Login</title>
    <meta name="login" content="Log in." />
</svelte:head>

<div class="text-column">
    <h1>Login</h1>

    <LoginForm />

    <p>Don't have an account? <a href="/register">Register</a></p>
</div>
