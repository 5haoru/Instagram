"""
Instagram Agent Runner
======================
Integrates GUI Agent to automatically execute test instructions.

Usage:
    python agent_runner.py --agent-name GPT-5 --device-id emulator-5554
    python agent_runner.py --agent-name GPT-5 -d emulator-5554 --task-ids 1 5 10
    python agent_runner.py --agent-name GPT-5 -d emulator-5554 --range 1-10
"""

import argparse
import json
import logging
import os
import sys
import time
from typing import Optional

from dotenv import load_dotenv

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from common import APP_PACKAGE, get_adb, get_ui

# Load environment variables
load_dotenv(override=True, verbose=True)

# Import agent factory (will be created)
try:
    from agent_factory import AgentEnum, create_agent
except ImportError:
    print("Error: agent_factory.py not found. Please create it first.")
    sys.exit(1)

# Import instructions and check runner
from run_checks import INSTRUCTIONS, DIFFICULTY_NAMES, run_single_check


def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser(
        description="Instagram Simulator Agent Runner",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Example Usage:
  Configure API_KEY and API_BASE in environment variables or .env file

  python agent_runner.py --agent-name GPT-5 --device-id emulator-5554
  python agent_runner.py --agent-name GPT-5 -d emulator-5554 --task-ids 1 5 10
  python agent_runner.py --agent-name GPT-5 -d emulator-5554 --range 1-10
  python agent_runner.py --agent-name GPT-5 -d emulator-5554 --difficulty 1
        """,
    )

    parser.add_argument(
        "--agent-name",
        type=str,
        required=True,
        help="Agent name (e.g., GPT-5, Gemini-2.5-Pro, Claude-4.5-Sonnet)",
    )

    parser.add_argument("--device-id", "-d", type=str, required=True, help="Device ID")

    parser.add_argument(
        "--task-ids",
        nargs="*",
        type=int,
        help="Specific task IDs to run (e.g., 1 5 10)",
    )

    parser.add_argument(
        "--range",
        type=str,
        help="Task range to run (e.g., 1-10)",
    )

    parser.add_argument(
        "--difficulty",
        type=int,
        choices=[1, 2, 3],
        help="Filter by difficulty level",
    )

    parser.add_argument(
        "--output-dir",
        type=str,
        default="./agent_output/",
        help="Output directory (default: ./agent_output/)",
    )

    parser.add_argument(
        "--max-steps",
        type=int,
        default=15,
        help="Maximum steps per task (default: 15)",
    )

    parser.add_argument("--verbose", action="store_true", help="Enable verbose mode")

    return parser.parse_args()


def clear_app_data(device_id: str, package_name: str):
    """Clear app data and restart app"""
    adb = get_adb(device_id)
    logging.info(f"Clearing app data for {package_name}...")
    adb.force_stop(package_name)
    adb.shell(f"pm clear {package_name}")
    time.sleep(2)
    adb.launch_app(package_name)
    time.sleep(3)
    logging.info("App restarted with clean state")


def run_agent_task(
    agent,
    task_id: int,
    instruction: str,
    difficulty: int,
    device_id: str,
    output_dir: str,
    verify_func=None,
):
    """Run a single task with agent"""
    diff_name = DIFFICULTY_NAMES.get(difficulty, "Unknown")

    logging.info(f"\n{'='*60}")
    logging.info(f"Task #{task_id:02d} ({diff_name}): {instruction}")
    logging.info(f"{'='*60}")

    # Clear app data before each task
    clear_app_data(device_id, APP_PACKAGE)

    # Reset agent
    agent.reset()

    # Execute instruction
    start_time = time.time()
    try:
        result = agent.execute_instruction(instruction)
        execution_time = time.time() - start_time

        task_result = {
            "id": task_id,
            "instruction": instruction,
            "difficulty": difficulty,
            "execution_time": execution_time,
            "agent_result": result.model_dump(),
            "verify_result": None,
        }

        # Always run independent check script verification
        logging.info("Running check script verification...")
        try:
            adb = get_adb(device_id)
            check_result = run_single_check(task_id, adb)
            task_result["verify_result"] = check_result
            if check_result["passed"]:
                logging.info(f"  ✅ Check PASSED: {check_result['message']}")
            else:
                logging.info(f"  ❌ Check FAILED: {check_result['message']}")
        except Exception as e:
            logging.error(f"  Check script error: {e}")
            task_result["verify_result"] = {"id": task_id, "passed": False, "message": f"Check error: {e}"}

        if result.success:
            logging.info("Agent reported: success")
        else:
            logging.error(f"Agent reported: failed - {result.error}")

        return task_result

    except Exception as e:
        logging.error(f"❌ Task execution error: {e}")
        return {
            "id": task_id,
            "instruction": instruction,
            "difficulty": difficulty,
            "execution_time": time.time() - start_time,
            "agent_result": {"success": False, "error": str(e)},
            "verify_result": None,
        }


def main():
    args = parse_args()

    # Setup logging
    logging_level = logging.DEBUG if args.verbose else logging.INFO
    logging.basicConfig(
        level=logging_level,
        format="%(asctime)s - %(levelname)s - %(message)s",
    )

    # Determine task IDs to run
    task_ids = []
    if args.task_ids:
        task_ids = args.task_ids
    elif args.range:
        start, end = map(int, args.range.split("-"))
        task_ids = list(range(start, end + 1))
    elif args.difficulty:
        task_ids = [
            tid for tid, (_, diff) in INSTRUCTIONS.items() if diff == args.difficulty
        ]
    else:
        task_ids = list(range(1, 41))

    # Filter valid IDs
    task_ids = [tid for tid in task_ids if 1 <= tid <= 40]

    if not task_ids:
        logging.error("No valid task IDs")
        sys.exit(1)

    # Create output directory
    os.makedirs(args.output_dir, exist_ok=True)
    screenshots_dir = os.path.join(args.output_dir, "screenshots", APP_PACKAGE)
    os.makedirs(screenshots_dir, exist_ok=True)

    # Create agent
    try:
        agent = create_agent(
            agent_name=args.agent_name,
            device_id=args.device_id,
            screenshots_dir=screenshots_dir,
            max_steps=args.max_steps,
        )
        logging.info(f"✅ Agent initialized: {args.agent_name}")
        logging.info(f"   Device ID: {args.device_id}")
        logging.info(f"   Output directory: {args.output_dir}")
        logging.info(f"   Screenshots directory: {screenshots_dir}")
        logging.info(f"   Total tasks: {len(task_ids)}")
    except Exception as e:
        logging.error(f"❌ Agent initialization failed: {e}")
        sys.exit(1)

    # Create output file
    output_filename = f"agent_results_{time.strftime('%Y%m%d_%H%M%S')}.jsonl"
    output_path = os.path.join(args.output_dir, output_filename)
    logging.info(f"Results will be saved to: {output_path}")

    # Run tasks
    results = []
    for task_id in task_ids:
        instruction, difficulty = INSTRUCTIONS[task_id]

        task_result = run_agent_task(
            agent=agent,
            task_id=task_id,
            instruction=instruction,
            difficulty=difficulty,
            device_id=args.device_id,
            output_dir=args.output_dir,
        )

        results.append(task_result)

        # Save result immediately
        with open(output_path, "a", encoding="utf-8") as f:
            f.write(json.dumps(task_result, ensure_ascii=False) + "\n")

    # Close agent
    try:
        agent.close()
    except Exception as e:
        logging.error(f"Error closing agent: {e}")

    # Summary
    logging.info(f"\n{'='*60}")
    logging.info("EXECUTION SUMMARY")
    logging.info(f"{'='*60}")

    # Use verify_result (check script) as the authoritative pass/fail
    def is_verified(r):
        vr = r.get("verify_result")
        if isinstance(vr, dict):
            return vr.get("passed", False)
        return False

    verified_pass = sum(1 for r in results if is_verified(r))
    agent_pass = sum(1 for r in results if r["agent_result"].get("success", False))
    total = len(results)

    logging.info(f"Total tasks: {total}")
    logging.info(f"Agent reported success: {agent_pass}")
    logging.info(f"Check script verified: {verified_pass}")
    logging.info(f"Verified pass rate: {verified_pass/total*100:.1f}%")

    # Statistics by difficulty
    for diff in [1, 2, 3]:
        diff_results = [r for r in results if r["difficulty"] == diff]
        if diff_results:
            diff_verified = sum(1 for r in diff_results if is_verified(r))
            diff_agent = sum(1 for r in diff_results if r["agent_result"].get("success", False))
            logging.info(
                f"{DIFFICULTY_NAMES[diff]}: agent={diff_agent}/{len(diff_results)}, verified={diff_verified}/{len(diff_results)}"
            )

    # List failed verifications
    failed = [r for r in results if not is_verified(r)]
    if failed:
        logging.info(f"\nFailed verifications:")
        for r in failed:
            vr = r.get("verify_result", {})
            msg = vr.get("message", "No check result") if isinstance(vr, dict) else str(vr)
            agent_ok = "agent:OK" if r["agent_result"].get("success", False) else "agent:FAIL"
            logging.info(f"  #{r['id']:02d} [{agent_ok}] {msg}")

    logging.info(f"\nAll results saved to: {output_path}")
    logging.info(f"{'='*60}\n")


if __name__ == "__main__":
    main()
