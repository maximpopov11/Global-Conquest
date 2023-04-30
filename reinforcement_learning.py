import torch
import torch.nn as nn
import torch.optim as optim
import random
from collections import deque


class DQNAgent:
    def __init__(self, state_space_size, action_space_size, learning_rate=0.001, discount_factor=0.99,
                 batch_size=64, memory_size=100000, exploration_rate=1.0, exploration_decay_rate=0.99995):
        self.state_space_size = state_space_size
        self.action_space_size = action_space_size
        self.learning_rate = learning_rate
        self.discount_factor = discount_factor
        self.batch_size = batch_size
        self.memory_size = memory_size
        self.exploration_rate = exploration_rate
        self.exploration_decay_rate = exploration_decay_rate
        self.memory = deque(maxlen=memory_size)
        self.device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
        self.q_network = self.build_model().to(self.device)
        self.optimizer = optim.Adam(self.q_network.parameters(), lr=learning_rate)

    def build_model(self):
        model = nn.Sequential(
            nn.Linear(self.state_space_size, 128),
            nn.ReLU(),
            nn.Linear(128, 128),
            nn.ReLU(),
            nn.Linear(128, self.action_space_size)
        )
        return model

    def remember(self, state, action, reward, next_state, done):
        self.memory.append((state, action, reward, next_state, done))

    def get_action(self, state):
        if random.uniform(0, 1) < self.exploration_rate:
            return random.randrange(self.action_space_size)
        else:
            with torch.no_grad():
                state = torch.tensor(state).float().unsqueeze(0).to(self.device)
                q_values = self.q_network(state)
                return q_values.argmax().item()

    def train(self, num_episodes=1000, max_steps_per_episode=1000, batch_size=64, discount_factor=0.99,
              target_update_frequency=10, epsilon_decay=0.99):
        # Loop over episodes
        for i in range(num_episodes):
            # Reset environment and get initial state
            state = self.env.reset()
            # Reset flag and start iterating until episode ends
            done = False
            total_reward = 0
            step = 0
            while not done and step < max_steps_per_episode:
                # Determine next action
                action = self.get_action(state)
                # Take action and observe reward and next state
                next_state, reward, done, _ = self.env.step(action)
                # Remember the transition
                self.remember(state, action, reward, next_state, done)
                # Update the DQN agent
                if len(self.memory) > batch_size:
                    self.learn(batch_size, discount_factor)
                # Update the total reward and state
                total_reward += reward
                state = next_state
                step += 1
                # Update the target network
                if step % target_update_frequency == 0:
                    self.update_target_network()
                # Decay epsilon
                self.epsilon *= epsilon_decay
            # Print the episode's results
            print("Episode {}: Total Reward = {}, Steps = {}".format(i + 1, total_reward, step))

        torch.save(self.q_network.state_dict(), 'model.pt')
