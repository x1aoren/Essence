# Welcome to the Essence Contributor Guide!
Thanks for contributing to Essence! We really appreciate you helping us out.

# What can I do?
We usually have a full list of tasks scheduled for the next update in our [milestones page](https://github.com/LewMC/Essence/milestones).
If this is empty, you can also take a look at our [issues](https://github.com/LewMC/Essence/issues), or add anything else you think would be a good fit!

## Testing
Please ensure you test all your additions in as many ways as you can.
All of our automated tests must pass, and we ask that you go in-game to check they work too.
The way we do it is we try to break it.
Try to make it do things it shouldn't.
That's usually a good indicator to if it works, but every feature is different and you should test it how you see fit.

# Where to Merge
## Bugfixes and features for new updates
If you're adding bugfixes or features, please merge them into the `next-update` branch.
This helps us to ensure that our snapshot builds are labelled as snapshot, so that it is clear to users download them that they are still in development.

## Changes to workflows, templates, etc.
You can merge these either into `next-update` if you'd like to wait until we next push an update for them to be applied, or into `main` if you'd like them to be applied immediately.
