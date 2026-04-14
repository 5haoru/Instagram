# Instagram Simulator - Automated Test Scripts

This directory contains automated test scripts for the Instagram Simulator Android app.

## Overview

- **40 test cases** covering various Instagram features
- **3 difficulty levels**: Easy (10), Medium (20), Hard (10)
- **Automated validation** using ADB and UI Automator

## Quick Start

### Prerequisites

- Python 3.x
- ADB (Android Debug Bridge) installed and in PATH
- Android device/emulator connected via ADB
- Instagram Simulator app installed on device
- (Optional) OpenAI-compatible API for GUI Agent automation

### Manual Testing

Run verification scripts to check if tasks were completed correctly:

```bash
python run_checks.py
```

### Run Specific Tests

```bash
# Run single test
python run_checks.py 1

# Run multiple tests
python run_checks.py 1 5 10

# Run range of tests
python run_checks.py --range 1-10

# Run by difficulty level
python run_checks.py --difficulty 1  # Easy
python run_checks.py --difficulty 2  # Medium
python run_checks.py --difficulty 3  # Hard
```

### Specify Device

```bash
python run_checks.py -d DEVICE_ID
```

### Generate JSON Report

```bash
python run_checks.py --output report.json
```

## Automated Testing with GUI Agent

### Setup

1. Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

2. Configure your API credentials in `.env`:
```bash
# Example for GPT-4o
GPT4O_API_KEY=sk-your-api-key-here
GPT4O_API_BASE=https://api.openai.com/v1
GPT4O_MODEL_NAME=gpt-4o
```

3. Install required dependencies:
```bash
pip install openai python-dotenv pillow
```

### Run Agent Tests

```bash
# Run all tests with GPT-4o
python agent_runner.py --agent-name GPT-4o --device-id emulator-5554

# Run specific tests
python agent_runner.py --agent-name GPT-4o -d emulator-5554 --task-ids 1 5 10

# Run by difficulty
python agent_runner.py --agent-name GPT-4o -d emulator-5554 --difficulty 1

# Run range of tests
python agent_runner.py --agent-name GPT-4o -d emulator-5554 --range 1-10

# Specify output directory
python agent_runner.py --agent-name GPT-4o -d emulator-5554 --output-dir ./results/
```

### Supported Agents

- `GPT-4o` - OpenAI GPT-4o
- `GPT-5` - OpenAI GPT-5 (if available)
- `Gemini-2.5-Pro` - Google Gemini 2.5 Pro
- `Claude-4.5-Sonnet` - Anthropic Claude 4.5 Sonnet
- `Claude-3.5-Sonnet` - Anthropic Claude 3.5 Sonnet

### Agent Output

Results are saved in JSONL format with:
- Task ID and instruction
- Execution time
- Agent reasoning and actions
- Screenshots at each step
- Success/failure status

Example output structure:
```
agent_output/
├── agent_results_20260413_143022.jsonl
└── screenshots/
    └── com.example.myinstagram/
        ├── screenshot_20260413_143025_001.png
        ├── screenshot_20260413_143028_002.png
        └── ...
```

## Test Instructions

See [instruction.md](instruction.md) for the complete list of 40 test instructions.

## Project Structure

```
AutoTest/
├── run_checks.py          # Manual test verification runner
├── agent_runner.py        # Automated agent test runner
├── agent_factory.py       # Agent creation factory
├── basic_agent.py         # Basic agent implementation
├── common.py              # Common utilities (ADB, UI parsing)
├── check_01.py - check_40.py  # Individual verification scripts
├── instruction.md         # Test instruction reference
├── .env.example           # Environment variable template
└── README.md             # This file
```

## Writing New Test Scripts

Each test script follows this template:

```python
"""
Check Script #XX: [Instruction description]
Difficulty: [1/2/3] ([Easy/Medium/Hard])
Check Method: [How the test validates success]
"""
import sys, os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from common import *


def check(adb, ui):
    # Your test logic here
    if success_condition:
        return result_pass("Success message")
    else:
        return result_fail("Failure message")


if __name__ == "__main__":
    run_check(check)
```

## Common Utilities

The `common.py` module provides:

### ADB Class
- `shell(cmd)` - Execute shell command
- `tap(x, y)` - Tap at coordinates
- `swipe(x1, y1, x2, y2)` - Swipe gesture
- `input_text(text)` - Input text
- `press_back()` - Press back button
- `get_ui_xml()` - Get UI hierarchy XML

### UI Class
- `has_text(text)` - Check if text exists
- `find_by_text(text)` - Find nodes by exact text
- `find_by_text_contains(text)` - Find nodes by partial text
- `find_by_desc(desc)` - Find nodes by content-desc
- `find_by_resource_id(rid)` - Find nodes by resource-id
- `get_node_bounds(node)` - Get node center coordinates

### Helper Functions
- `result_pass(msg)` - Return pass result
- `result_fail(msg)` - Return fail result
- `get_adb(device_id)` - Get ADB instance
- `get_ui(adb)` - Get UI parser instance

## Example Usage

```python
# Check if on homepage
if not ui.has_text("Instagram"):
    return result_fail("Not on homepage")

# Find and tap a button
nodes = ui.find_by_desc("Like")
if nodes:
    x, y = ui.get_node_bounds(nodes[0])
    adb.tap(x, y)
    return result_pass("Like button tapped")
```

## Tips

1. Always verify the current screen before performing actions
2. Use `time.sleep()` after actions to allow UI to update
3. Check multiple indicators for success (text, desc, attributes)
4. Provide clear pass/fail messages for debugging
5. Handle edge cases (already liked, not found, etc.)

## License

This project is part of the Instagram Simulator educational app.
