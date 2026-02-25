from locust import HttpUser, TaskSet, task, between
import random
import numpy as np

# Zipf distribution parameter (s): determines how concentrated the accesses are to popular posts
s = 0.9 

# Generate probability distribution for 10,000 posts using Zipf's law
N=10000
ranks = np.arange(1, N + 1)
weights = 1 / np.power(ranks, s)
prob = weights / weights.sum()

class PostCache(TaskSet):
    token = None

    @task
    def view_post(self):
        post_id = np.random.choice(N, p=prob) # Select post_id according to Zipf distribution (popular posts more likely)

        self.client.get(
            f"/posts/{post_id}",
            name="/posts/[id]"
        )

class WebsiteUser(HttpUser):
    tasks = [PostCache]
    wait_time = between(0,1) # Time between requests