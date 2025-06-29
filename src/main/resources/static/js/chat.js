document.addEventListener('DOMContentLoaded', () => {
    const chatContainer = document.getElementById('chatContainer');
    const userInput = document.getElementById('userInput');
    const sendButton = document.getElementById('sendButton');

    const addMessage = (content, isUser) => {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isUser ? 'user-message' : 'ai-message'}`;

        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';

        const avatar = document.createElement('div');
        avatar.className = `avatar ${isUser ? 'user-avatar' : 'ai-avatar'}`;
        avatar.innerHTML = isUser ? '<i class="fas fa-user"></i>' : '<i class="fas fa-robot"></i>';

        const text = document.createElement('div');
        text.className = 'text';

        if (isUser) {
            // For user messages, always use textContent to prevent XSS attacks
            text.textContent = content;
        } else {
            // For AI messages, parse for <think> tags and render as HTML.
            // This is safe because we control the replacement tag (<i>).
            // The regex captures content between <think> tags and wraps it in <i> tags.
            const formattedContent = content.replace(/<think>([\s\S]*?)<\/think>/g, '<i>$1</i>');
            text.innerHTML = formattedContent;
        }

        messageContent.appendChild(avatar);
        messageContent.appendChild(text);
        messageDiv.appendChild(messageContent);
        chatContainer.appendChild(messageDiv);

        // Scroll to bottom
        chatContainer.scrollTop = chatContainer.scrollHeight;
    };

    const sendMessage = () => {
        const message = userInput.value.trim();
        if (message) {
            addMessage(message, true);
            userInput.value = '';
            userInput.focus();

            // Send message to server
            fetch('/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `content=${encodeURIComponent(message)}`
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.text();
            })
            .then(data => {
                addMessage(data, false);
            })
            .catch(error => {
                addMessage('Error: Could not get response from AI.', false);
                console.error('Error:', error);
            });
        }
    };

    sendButton.addEventListener('click', sendMessage);

    userInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });
});