"""
Run tests with the ScriptedAgent using predefined action sequences.
Usage:
    python run_scripted_tests.py
    python run_scripted_tests.py --difficulty easy
    python run_scripted_tests.py --ids 1 2 3
"""

import sys
import os

sys.path.insert(0, os.path.dirname(__file__))

from test_runner import TestRunner
from gui_agent_adapter import ScriptedAgent
from scripted_actions import get_scripted_actions


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Run scripted AutoTests")
    parser.add_argument("--device", "-d", help="ADB device ID")
    parser.add_argument("--tests", "-t", default="test_instructions.json",
                        help="Path to test instructions JSON")
    parser.add_argument("--difficulty", choices=["easy", "medium", "hard", "expert"],
                        help="Filter by difficulty level")
    parser.add_argument("--category", help="Filter by category")
    parser.add_argument("--ids", nargs="+", type=int, help="Run specific test IDs")
    parser.add_argument("--no-reset", action="store_true",
                        help="Do not reset app between tests")
    parser.add_argument("--report", default="test_report.json",
                        help="Output report file path")

    args = parser.parse_args()

    # Initialize scripted agent
    action_scripts = get_scripted_actions()
    agent = ScriptedAgent(action_scripts)

    # Initialize test runner
    runner = TestRunner(device_id=args.device)
    tests = runner.load_tests(args.tests)

    # Setup and run
    runner.setup()
    runner.run_tests(
        tests,
        gui_agent_callback=agent.execute,
        difficulty_filter=args.difficulty,
        category_filter=args.category,
        test_ids=args.ids,
        reset_between_tests=not args.no_reset,
    )
    runner.print_report()
    runner.save_report(args.report)


if __name__ == "__main__":
    main()
