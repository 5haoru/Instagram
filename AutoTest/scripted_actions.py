"""
Scripted Test Actions - Predefined action sequences for each test case.
=======================================================================
Maps each test instruction to a sequence of UI actions that can be
executed by the ScriptedAgent for automated regression testing.

Coordinate values are for a 1080x2340 resolution device.
Adjust coordinates for different screen sizes.
"""

# Screen dimensions (default: 1080x2340)
SCREEN_W = 1080
SCREEN_H = 2340

# Bottom navigation bar tap positions (5 tabs evenly distributed)
NAV_HOME = {"action": "tap", "x": 108, "y": 2260}
NAV_SEARCH = {"action": "tap", "x": 324, "y": 2260}
NAV_REELS = {"action": "tap", "x": 540, "y": 2260}
NAV_MESSAGES = {"action": "tap", "x": 756, "y": 2260}
NAV_PROFILE = {"action": "tap", "x": 972, "y": 2260}

# Common wait actions
WAIT_SHORT = {"action": "wait", "seconds": 0.5}
WAIT_MEDIUM = {"action": "wait", "seconds": 1.0}
WAIT_LONG = {"action": "wait", "seconds": 2.0}

# Common actions
BACK = {"action": "back"}
ENTER = {"action": "enter"}

# Home screen top bar
HOME_NEW_POST = {"action": "tap", "x": 50, "y": 80}
HOME_NOTIFICATIONS = {"action": "tap", "x": 1030, "y": 80}

# Post action buttons (approximate positions for first post)
POST1_LIKE = {"action": "tap", "x": 60, "y": 900}
POST1_COMMENT = {"action": "tap", "x": 160, "y": 900}
POST1_SHARE = {"action": "tap", "x": 260, "y": 900}
POST1_REPOST = {"action": "tap", "x": 360, "y": 900}
POST1_SAVE = {"action": "tap", "x": 1030, "y": 900}
POST1_MENU = {"action": "tap", "x": 1030, "y": 500}
POST1_USERNAME = {"action": "tap", "x": 200, "y": 500}

# Swipe actions
SWIPE_UP_REEL = {"action": "swipe", "x1": 540, "y1": 1600, "x2": 540, "y2": 400, "duration": 300}


def get_scripted_actions():
    """
    Returns a dict mapping instruction text -> list of action dicts.
    These are used by the ScriptedAgent for fully automated testing.
    """
    return {
        # ===== EASY (1-10) =====

        # Test 1: Click the search tab
        "Click the search tab in the bottom navigation bar": [
            NAV_SEARCH,
            WAIT_MEDIUM,
        ],

        # Test 2: Click the Reels tab
        "Click the Reels tab in the bottom navigation bar": [
            NAV_REELS,
            WAIT_MEDIUM,
        ],

        # Test 3: Click the Messages tab
        "Click the Messages tab in the bottom navigation bar": [
            NAV_MESSAGES,
            WAIT_MEDIUM,
        ],

        # Test 4: Click the Profile tab
        "Click the Profile tab in the bottom navigation bar": [
            NAV_PROFILE,
            WAIT_MEDIUM,
        ],

        # Test 5: Like the first post
        "Like the first post in the home feed": [
            WAIT_SHORT,
            POST1_LIKE,
            WAIT_MEDIUM,
        ],

        # Test 6: Save the first post
        "Save the first post in the home feed": [
            WAIT_SHORT,
            POST1_SAVE,
            WAIT_MEDIUM,
        ],

        # Test 7: Click notification icon
        "Click the notification icon (heart) on the home screen top bar": [
            HOME_NOTIFICATIONS,
            WAIT_MEDIUM,
        ],

        # Test 8: Click home tab
        "Click on the home tab to return to the home feed": [
            NAV_HOME,
            WAIT_MEDIUM,
        ],

        # Test 9: Click new post icon
        "Click the new post icon (plus icon) on the home screen top bar": [
            HOME_NEW_POST,
            WAIT_MEDIUM,
        ],

        # Test 10: Swipe up on Reels
        "Swipe up on the Reels screen to view the next reel": [
            NAV_REELS,
            WAIT_LONG,
            SWIPE_UP_REEL,
            WAIT_MEDIUM,
        ],

        # ===== MEDIUM (11-20) =====

        # Test 11: Navigate to Profile > Edit profile
        "Navigate to the Profile tab and click 'Edit profile' button": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Edit profile"},
            WAIT_MEDIUM,
        ],

        # Test 12: Search for 'john'
        "Open the search tab and type 'john' in the search bar": [
            NAV_SEARCH,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Search"},
            WAIT_SHORT,
            {"action": "type", "text": "john"},
            WAIT_MEDIUM,
        ],

        # Test 13: Click first conversation
        "Click on the first conversation in the Messages screen": [
            NAV_MESSAGES,
            WAIT_MEDIUM,
            {"action": "tap", "x": 540, "y": 500},  # First conversation item
            WAIT_MEDIUM,
        ],

        # Test 14: Open comments on first post
        "Click the comment icon on the first post and view comments": [
            POST1_COMMENT,
            WAIT_MEDIUM,
        ],

        # Test 15: Share first post
        "Click the share icon on the first post in the home feed": [
            POST1_SHARE,
            WAIT_MEDIUM,
        ],

        # Test 16: Profile > Share profile
        "Navigate to Profile and click 'Share profile' button": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Share profile"},
            WAIT_MEDIUM,
        ],

        # Test 17: Three-dot menu > Not interested
        "Click the three-dot menu on the first post and select 'Not interested'": [
            POST1_MENU,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Not interested"},
            WAIT_MEDIUM,
        ],

        # Test 18: Profile > Followers count
        "Navigate to Profile and click on the Followers count": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Followers"},
            WAIT_MEDIUM,
        ],

        # Test 19: Like a reel
        "Like a reel on the Reels screen": [
            NAV_REELS,
            WAIT_LONG,
            {"action": "tap", "x": 1020, "y": 900},  # Heart icon on right side
            WAIT_MEDIUM,
        ],

        # Test 20: Profile > Settings > Saved
        "Navigate to Profile, click the settings icon (hamburger menu), then go to 'Saved'": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},  # Hamburger menu icon
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Saved"},
            WAIT_MEDIUM,
        ],

        # ===== HARD (21-30) =====

        # Test 21: Comment on post
        "Open the comment section on the first post, type 'Nice photo!' and post the comment": [
            POST1_COMMENT,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Add a comment..."},
            WAIT_SHORT,
            {"action": "type", "text": "Nice photo!"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Post"},
            WAIT_MEDIUM,
        ],

        # Test 22: Send a message
        "Open a chat conversation and send the message 'Hello, how are you?'": [
            NAV_MESSAGES,
            WAIT_MEDIUM,
            {"action": "tap", "x": 540, "y": 500},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Message..."},
            WAIT_SHORT,
            {"action": "type", "text": "Hello, how are you?"},
            WAIT_SHORT,
            {"action": "tap_desc", "desc": "Send"},
            WAIT_MEDIUM,
        ],

        # Test 23: Settings > Account Privacy > Toggle private
        "Navigate to Settings > Account Privacy and toggle the Private account switch on": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Account privacy"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Private account"},
            WAIT_MEDIUM,
        ],

        # Test 24: Settings > Time Management > Daily time limit
        "Navigate to Settings > Time Management and enable the daily time limit, then set it to 60 minutes": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Time management"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Set daily time limit"},
            WAIT_MEDIUM,
            # Slider interaction - approximate position for 60 min on range 15-180
            {"action": "tap", "x": 350, "y": 1200},  # Slider position
            WAIT_MEDIUM,
        ],

        # Test 25: Edit profile > Change name
        "Navigate to Profile, click Edit profile, and change the display name to 'Test User'": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Edit profile"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Name"},
            WAIT_SHORT,
            {"action": "clear", "length": 30},
            {"action": "type", "text": "Test User"},
            WAIT_MEDIUM,
        ],

        # Test 26: Follow user + verify count
        "Click 'Follow' on a user in the home feed, then navigate to the Profile tab and check the Following count increased": [
            {"action": "tap_text", "text": "Follow"},
            WAIT_MEDIUM,
            NAV_PROFILE,
            WAIT_MEDIUM,
        ],

        # Test 27: Settings > Blocked > Block user
        "Navigate to Settings > Blocked, click the '+' icon, search for a user and block them": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Blocked"},
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},  # + icon
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Search"},
            WAIT_SHORT,
            {"action": "type", "text": "j"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Block"},
            WAIT_MEDIUM,
        ],

        # Test 28: Settings > Close Friends > Add user
        "Navigate to Settings > Close Friends, search for a user and add them to close friends": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Close friends"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Add"},
            WAIT_MEDIUM,
        ],

        # Test 29: Profile > Followers > Remove
        "Navigate to Profile > Followers, then remove the first follower from the list": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Followers"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Remove"},
            WAIT_MEDIUM,
        ],

        # Test 30: Settings > Saved > Create collection
        "Navigate to Settings > Saved, click the '+' icon and create a new collection named 'Favorites'": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Saved"},
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},  # + icon
            WAIT_MEDIUM,
            {"action": "type", "text": "Favorites"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Create"},
            WAIT_MEDIUM,
        ],

        # ===== EXPERT (31-40) =====

        # Test 31: Create post with caption + location
        "Create a new post: select the second image from gallery, write caption 'Beautiful sunset #nature', add the location 'Central Park', then share the post": [
            HOME_NEW_POST,
            WAIT_MEDIUM,
            {"action": "tap", "x": 400, "y": 1400},  # Second image in grid
            WAIT_SHORT,
            {"action": "tap_text", "text": "Next"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Caption"},
            WAIT_SHORT,
            {"action": "type", "text": "Beautiful sunset #nature"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Add location"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Central Park"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Share"},
            WAIT_LONG,
        ],

        # Test 32: Create post with poll
        "Create a new post: select any image, write a caption, add a poll with question 'Which is better?' and options 'Option A' and 'Option B', then share the post": [
            HOME_NEW_POST,
            WAIT_MEDIUM,
            {"action": "tap", "x": 150, "y": 1400},  # First image
            WAIT_SHORT,
            {"action": "tap_text", "text": "Next"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Caption"},
            WAIT_SHORT,
            {"action": "type", "text": "Vote now!"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Poll"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Ask a question"},
            WAIT_SHORT,
            {"action": "type", "text": "Which is better?"},
            WAIT_SHORT,
            {"action": "tap", "x": 540, "y": 700},  # Option 1 field
            {"action": "type", "text": "Option A"},
            WAIT_SHORT,
            {"action": "tap", "x": 540, "y": 800},  # Option 2 field
            {"action": "type", "text": "Option B"},
            WAIT_SHORT,
            {"action": "tap_desc", "desc": "Done"},  # Check icon
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Share"},
            WAIT_LONG,
        ],

        # Test 33: Create post with music + audience
        "Create a new post: select any image, write a caption, add music by searching for a track, set the audience to 'Close Friends', then share the post": [
            HOME_NEW_POST,
            WAIT_MEDIUM,
            {"action": "tap", "x": 150, "y": 1400},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Next"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Caption"},
            WAIT_SHORT,
            {"action": "type", "text": "Vibes"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Add music"},
            WAIT_MEDIUM,
            {"action": "tap", "x": 540, "y": 500},  # First track
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Audience"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Close Friends"},
            WAIT_SHORT,
            {"action": "tap_desc", "desc": "Done"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Share"},
            WAIT_LONG,
        ],

        # Test 34: Like then unlike
        "Like the first post, then unlike it, and verify the heart icon returns to its original state": [
            POST1_LIKE,
            WAIT_MEDIUM,
            POST1_LIKE,
            WAIT_MEDIUM,
        ],

        # Test 35: Save post + verify in Saved
        "Save a post from the home feed, then navigate to Settings > Saved and verify the post appears in 'All Posts'": [
            POST1_SAVE,
            WAIT_MEDIUM,
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Saved"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "All Posts"},
            WAIT_MEDIUM,
        ],

        # Test 36: Follow user from profile + verify in Following
        "Follow a user from their profile page, then navigate to your own Profile > Following tab and verify they appear in the following list": [
            POST1_USERNAME,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Follow"},
            WAIT_MEDIUM,
            BACK,
            WAIT_SHORT,
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Following"},
            WAIT_MEDIUM,
        ],

        # Test 37: Create post with more options + share to
        "Create a new post with a caption, enable 'Hide like and view counts' and 'Turn off commenting' in More Options, toggle sharing to Facebook in 'Also share to', then publish the post": [
            HOME_NEW_POST,
            WAIT_MEDIUM,
            {"action": "tap", "x": 150, "y": 1400},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Next"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Caption"},
            WAIT_SHORT,
            {"action": "type", "text": "Test post"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "More options"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Hide like and view counts"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Turn off commenting"},
            WAIT_SHORT,
            BACK,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Also share to..."},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Facebook"},
            WAIT_SHORT,
            BACK,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Share"},
            WAIT_LONG,
        ],

        # Test 38: Edit multiple profile fields
        "Navigate to Profile > Edit profile, change the Name to 'New Name', change the Bio to 'Hello World', change the Username to 'newuser', then close the edit screen and verify the profile shows the updated information": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Edit profile"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Name"},
            {"action": "clear", "length": 30},
            {"action": "type", "text": "New Name"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Username"},
            {"action": "clear", "length": 30},
            {"action": "type", "text": "newuser"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Bio"},
            {"action": "clear", "length": 50},
            {"action": "type", "text": "Hello World"},
            WAIT_SHORT,
            BACK,
            WAIT_MEDIUM,
        ],

        # Test 39: Block + verify + unblock
        "Block a user from Settings > Blocked, then navigate to that user's profile from the home feed and verify their profile shows as private/restricted. Finally, unblock them from Settings > Blocked": [
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Blocked"},
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "type", "text": "j"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Block"},
            WAIT_MEDIUM,
            BACK,
            WAIT_SHORT,
            BACK,
            WAIT_SHORT,
            BACK,
            WAIT_SHORT,
            NAV_HOME,
            WAIT_MEDIUM,
            POST1_USERNAME,
            WAIT_MEDIUM,
            BACK,
            WAIT_SHORT,
            NAV_PROFILE,
            WAIT_MEDIUM,
            {"action": "tap", "x": 1030, "y": 80},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Blocked"},
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Unblock"},
            WAIT_MEDIUM,
        ],

        # Test 40: Complex multi-step workflow
        "Complete the following sequence: 1) Like and save the first post, 2) Open comments and add a comment 'Great post!', 3) Navigate to the post author's profile by clicking their username, 4) Follow the author, 5) Go back to home and verify the Follow button changed to 'Following'": [
            POST1_LIKE,
            WAIT_SHORT,
            POST1_SAVE,
            WAIT_SHORT,
            POST1_COMMENT,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Add a comment..."},
            WAIT_SHORT,
            {"action": "type", "text": "Great post!"},
            WAIT_SHORT,
            {"action": "tap_text", "text": "Post"},
            WAIT_MEDIUM,
            BACK,  # Close comment sheet
            WAIT_SHORT,
            POST1_USERNAME,
            WAIT_MEDIUM,
            {"action": "tap_text", "text": "Follow"},
            WAIT_MEDIUM,
            BACK,
            WAIT_SHORT,
            NAV_HOME,
            WAIT_MEDIUM,
        ],
    }
