# -*- coding:utf-8 -*-
"""
Instagram AutoTest Module
==========================
Integration module for Instagram detection scripts with AppSim dependencies.
"""

from typing import Callable, Any

# Import all check functions
from .check_01 import check as check_01
from .check_02 import check as check_02
from .check_03 import check as check_03
from .check_04 import check as check_04
from .check_05 import check as check_05
from .check_06 import check as check_06
from .check_07 import check as check_07
from .check_08 import check as check_08
from .check_09 import check as check_09
from .check_10 import check as check_10
from .check_11 import check as check_11
from .check_12 import check as check_12
from .check_13 import check as check_13
from .check_14 import check as check_14
from .check_15 import check as check_15
from .check_16 import check as check_16
from .check_17 import check as check_17
from .check_18 import check as check_18
from .check_19 import check as check_19
from .check_20 import check as check_20
from .check_21 import check as check_21
from .check_22 import check as check_22
from .check_23 import check as check_23
from .check_24 import check as check_24
from .check_25 import check as check_25
from .check_26 import check as check_26
from .check_27 import check as check_27
from .check_28 import check as check_28
from .check_29 import check as check_29
from .check_30 import check as check_30
from .check_31 import check as check_31
from .check_32 import check as check_32
from .check_33 import check as check_33
from .check_34 import check as check_34
from .check_35 import check as check_35
from .check_36 import check as check_36
from .check_37 import check as check_37
from .check_38 import check as check_38
from .check_39 import check as check_39
from .check_40 import check as check_40

# Instruction to difficulty mapping (from run_checks.py)
INSTRUCTIONS = {
    1:  ("Tell me how many likes the first post on the homepage has", 1),
    2:  ("Tell me how many likes the currently playing short video has", 1),
    3:  ("Tell me how many contacts are on the current messages page", 1),
    4:  ("Tell me the username of the current user", 1),
    5:  ("Like the first post on the homepage", 1),
    6:  ("Favorite the first post on the homepage", 1),
    7:  ("Open the notifications page", 1),
    8:  ("Go to the author profile of the first post on the homepage", 1),
    9:  ("Repost the first post", 1),
    10: ("Swipe to view the next short video", 1),
    11: ("Edit gender on profile to female", 2),
    12: ("Search for content related to 'happy' on the search page", 2),
    13: ("Open the first conversation on the messages page", 2),
    14: ("Show me the first comment of the first post on the homepage", 2),
    15: ("Share the first post on the homepage", 2),
    16: ("Share my personal QR code", 2),
    17: ("Mark the first post on the homepage as 'Not Interested'", 2),
    18: ("Check my number of followers", 2),
    19: ("Like the currently playing short video", 2),
    20: ("Check how many items are in my favorites collection", 2),
    21: ("Comment 'Nice!' under the second post on the homepage", 2),
    22: ("Open a chat and send the message 'Hello, how are you?'", 2),
    23: ("Set my account to private", 2),
    24: ("Set daily usage time limit to 60 minutes", 2),
    25: ("Change my username to 'zhou'", 2),
    26: ("Follow the author of the third post on the homepage", 2),
    27: ("Randomly select a user and block them", 2),
    28: ("Randomly add a close friend", 2),
    29: ("Remove a follower", 2),
    30: ("Enable Sleep Mode", 2),
    31: ("View the first video on my profile", 3),
    32: ("Log out of the current account", 3),
    33: ("Create a new collection named 'Favorites'", 3),
    34: ("Create a new post: select the second picture from the album, set title 'Beautiful sunset', add hashtag #nature, add location 'Central Park', then post", 3),
    35: ("Create a new post: select any picture, enter a title, add a poll with question 'Which is better?' and options 'Option A' and 'Option B', then post", 3),
    36: ("Create a new post: select any picture, enter a title, add a music track by search, set audience to 'Close Friends', then post", 3),
    37: ("Send 'I like your post!' to an unfollowed user", 3),
    38: ("Create a new post, hide like count, enable Facebook sharing", 3),
    39: ("Create a new post: select the second picture from the album, set title 'Beautiful sunset', add hashtag #nature, add location 'Central Park', hide like count, disable comments, then post", 3),
    40: ("Post a short video", 3),
}

# Check function mapping
CHECK_FUNCTIONS = {
    1: check_01,
    2: check_02,
    3: check_03,
    4: check_04,
    5: check_05,
    6: check_06,
    7: check_07,
    8: check_08,
    9: check_09,
    10: check_10,
    11: check_11,
    12: check_12,
    13: check_13,
    14: check_14,
    15: check_15,
    16: check_16,
    17: check_17,
    18: check_18,
    19: check_19,
    20: check_20,
    21: check_21,
    22: check_22,
    23: check_23,
    24: check_24,
    25: check_25,
    26: check_26,
    27: check_27,
    28: check_28,
    29: check_29,
    30: check_30,
    31: check_31,
    32: check_32,
    33: check_33,
    34: check_34,
    35: check_35,
    36: check_36,
    37: check_37,
    38: check_38,
    39: check_39,
    40: check_40,
}

# Package name for Instagram
INSTAGRAM_PACKAGE = "com.example.myinstagram"

# Difficulty names
DIFFICULTY_NAMES = {1: "Easy", 2: "Medium", 3: "Hard"}


def get_check_function(check_id: int) -> Callable:
    """Get check function by ID"""
    if check_id not in CHECK_FUNCTIONS:
        raise ValueError(f"Invalid check ID: {check_id}")
    return CHECK_FUNCTIONS[check_id]


def get_instruction(check_id: int) -> tuple:
    """Get instruction and difficulty by ID"""
    if check_id not in INSTRUCTIONS:
        raise ValueError(f"Invalid check ID: {check_id}")
    return INSTRUCTIONS[check_id]


__all__ = [
    'INSTRUCTIONS',
    'CHECK_FUNCTIONS',
    'INSTAGRAM_PACKAGE',
    'DIFFICULTY_NAMES',
    'get_check_function',
    'get_instruction',
]
