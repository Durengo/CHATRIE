import { apiBaseUrl, loginUsername } from '$lib/stores.js';

export async function verifyJWT(username, authToken) {
	try {
		// // Create a new Request object
		// const request = new Request(`http://localhost:8090/api/v1/auth/validate`, {
		// 	method: 'POST',
		// 	headers: {
		// 		'Content-Type': 'application/json',
		// 		Authorization: `Bearer ${authToken}`
		// 	},
		// 	body: JSON.stringify({ username: username, authToken })
		// });

		// // Log the entire request
		// console.log('Request:', request);

        // const str = JSON.stringify({ username: username, token: authToken.token });

        // console.log(`${str}`)

		// // Make the fetch call with the Request object
		// const response = await fetch(request);

        const requestBody = JSON.stringify({ username: username, token: authToken});

        console.log("Request body:" + requestBody);

		const response = await fetch(`http://localhost:8090/api/v1/auth/validate`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
                // ,
				// Authorization: `Bearer ${authToken.token}`
			},
			body: requestBody
		});

		if (response.ok) {
            const data = await response.json();
            const result = data;

            console.log("Result: " + result);

			return result;
		} else {
			const errorData = await response.json();
			console.error('Error verifying JWT:', errorData);

			console.error(`Username: ${username}, Token: ${authToken}`);

            console.error('JWT verification failed');

			return false;
		}
	} catch (error) {
		console.error('Error verifying JWT:', error);
		return false;
	}
}
