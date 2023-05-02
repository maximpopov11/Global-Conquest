import torch
import torch.nn as nn
import torch.optim as optim
import random
from collections import deque


class DQNAgent:
    def __init__(self, env, state_space_size, action_space_size, learning_rate=0.001, discount_factor=0.99,
                 batch_size=64, memory_size=100000, exploration_rate=1.0, exploration_decay_rate=0.99995):
        self.env = env
        self.state_space_size = state_space_size
        self.action_space_size = action_space_size
        self.discount_factor = discount_factor
        self.batch_size = batch_size
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

    def learn(self, batch_size, discount_factor):
        minibatch = random.sample(self.memory, batch_size)
        states_batch, action_batch, reward_batch, next_states_batch, done_batch = zip(*minibatch)

        states_batch = torch.tensor(states_batch).float().to(self.device)
        action_batch = torch.tensor(action_batch).to(self.device)
        reward_batch = torch.tensor(reward_batch).float().to(self.device)
        next_states_batch = torch.tensor(next_states_batch).float().to(self.device)
        done_batch = torch.tensor(done_batch).to(self.device)

        # Compute Q values for current states
        q_values = self.q_network(states_batch)
        q_values = q_values.gather(1, action_batch.unsqueeze(1)).squeeze(1)

        # Compute target Q values for next states
        target_q_values = self.q_network(next_states_batch)
        max_target_q_values = target_q_values.max(1)[0]
        target_q_values = reward_batch + (1 - done_batch) * discount_factor * max_target_q_values

        # Compute loss and update Q network
        loss = nn.MSELoss()(q_values, target_q_values.detach())
        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()

    def update_target_network(self):
        self.q_network.load_state_dict(self.q_network.state_dict())

    def train(self, num_episodes=1000, max_steps_per_episode=1000, target_update_frequency=10):
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
                if len(self.memory) > self.batch_size:
                    self.learn(self.batch_size, self.discount_factor)
                # Update the total reward and state
                total_reward += reward
                state = next_state
                step += 1
                # Update the target network
                if step % target_update_frequency == 0:
                    self.update_target_network()
                # Decay epsilon
                self.exploration_rate *= self.exploration_decay_rate
            # Print the episode's results
            print("Episode {}: Total Reward = {}, Steps = {}".format(i + 1, total_reward, step))

        torch.save(self.q_network.state_dict(), 'model.pt')
