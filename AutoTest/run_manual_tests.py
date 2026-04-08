"""
Run tests in manual/interactive mode.
The user performs each action manually, then presses Enter to verify.

Usage:
    python run_manual_tests.py
    python run_manual_tests.py --difficulty easy
    python run_manual_tests.py --ids 1 5 10
"""

import sys
import os

sys.path.insert(0, os.path.dirname(__file__))

from test_runner import TestRunner


def main():
    import argparse

    parser = argparse.ArgumentParser(description="Run manual AutoTests")
    parser.add_argument("--device", "-d", help="ADB device ID")
    parser.add_argument("--tests", "-t", default="test_instructions.json",
                        help="Path to test instructions JSON")
    parser.add_argument("--difficulty", choices=["easy", "medium", "hard", "expert"],
                        help="Filter by difficulty level")
    parser.add_argument("--category", help="Filter by category")
    parser.add_argument("--ids", nargs="+", type=int, help="Run specific test IDs")
    parser.add_argument("--no-reset", action="store_true",
                        help="Do not reset app between tests")
    parser.add_argument("--report", default="test_report_manual.json",
                        help="Output report file path")

    args = parser.parse_args()

    runner = TestRunner(device_id=args.device)
    tests = runner.load_tests(args.tests)

    runner.setup()
    runner.run_tests(
        tests,
        gui_agent_callback=None,  # Manual mode
        difficulty_filter=args.difficulty,
        category_filter=args.category,
        test_ids=args.ids,
        reset_between_tests=not args.no_reset,
    )
    runner.print_report()
    runner.save_report(args.report)


if __name__ == "__main__":
    main()
