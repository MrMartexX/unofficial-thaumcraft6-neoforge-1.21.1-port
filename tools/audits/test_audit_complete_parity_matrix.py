"""Regression tests for the honest TC6 master parity matrix summary."""

from __future__ import annotations

import runpy
import unittest
from pathlib import Path
from unittest.mock import patch


AUDIT = runpy.run_path(str(Path(__file__).with_name("audit-complete-parity-matrix.py")))


class MasterParitySummaryTests(unittest.TestCase):
    def _entry(
        self, id: str, *, legacy: bool, registered: bool, implementation: str,
        model: bool = False, kind: str = "item"
    ):
        return AUDIT["RegistryEntry"](
            id=id,
            kind=kind,
            legacy_registered=legacy,
            port_registered=registered,
            implementation=implementation,
            in_creative_tab=False,
            has_model=model,
            has_blockstate=False,
        )

    def _report(self, source: str, *, exists: bool = True):
        entries = [
            self._entry("legacy_real", legacy=True, registered=True,
                        implementation="REAL", model=True),
            self._entry("legacy_missing", legacy=True, registered=False,
                        implementation="MISSING"),
            self._entry("port_only", legacy=False, registered=True,
                        implementation="REAL", model=True),
        ]
        with patch.object(Path, "exists", return_value=exists):
            with patch.object(Path, "read_text", return_value=source):
                return AUDIT["generate_master_report"](
                    Path("/fixture"), Path("/port"), Path("/legacy"), entries,
                    [], [], [], (0, 0, 0), [], [], [],
                )

    def test_only_legacy_ids_affect_legacy_parity(self):
        markdown, payload = self._report(
            "### Overall: 28 / 28 checks passed (100.0%)."
        )
        self.assertEqual(payload["registry"]["total"], 2)
        self.assertEqual(payload["registry"]["registered"], 1)
        self.assertEqual(payload["registry"]["real"], 1)
        self.assertEqual(payload["registry"]["missing"], 1)
        self.assertEqual(payload["registry"]["models_ready"], 1)
        self.assertEqual(payload["registry"]["port_only"], 1)
        self.assertEqual(
            payload["registry"]["port_only_ids"],
            [{"kind": "item", "id": "port_only"}],
        )
        self.assertIn("Registered IDs | 1 | 2 | 50.0%", markdown)
        self.assertIn("Real Custom Classes | 1 | 2 | 50.0%", markdown)
        self.assertIn("Port-only registry IDs (excluded from legacy target): 1", markdown)

    def test_runtime_json_matches_actual_report(self):
        markdown, payload = self._report("### Overall: 28 / 28 checks passed (100.0%).")
        self.assertEqual(
            {key: payload["runtime"][key] for key in ("passed", "total", "percent", "status")},
            {"passed": 28, "total": 28, "percent": 100.0, "status": "OK"},
        )
        self.assertIn("Dedicated Server Verification | 28 / 28 | 28 | 100.0% | OK", markdown)

    def test_failed_runtime_report_is_discrepancy(self):
        markdown, payload = self._report("Overall: 26 / 28 checks passed")
        self.assertEqual(payload["runtime"]["passed"], 26)
        self.assertEqual(payload["runtime"]["total"], 28)
        self.assertEqual(payload["runtime"]["status"], "DISCREPANCY (2)")
        self.assertIn("92.9% | DISCREPANCY (2)", markdown)

    def test_missing_runtime_report_cannot_pass(self):
        markdown, payload = self._report("", exists=False)
        self.assertEqual(payload["runtime"]["status"], "UNVERIFIED")
        self.assertEqual(payload["runtime"]["total"], 0)
        self.assertEqual(payload["runtime"]["percent"], 0)
        self.assertIn("0 / 0 | 0 | 0.0% | UNVERIFIED", markdown)

    def test_invalid_or_zero_runtime_counts_cannot_pass(self):
        for report in ("", "Overall: 0 / 0 checks passed", "Overall: 30 / 28 checks passed"):
            with self.subTest(report=report):
                _, payload = self._report(report)
                self.assertEqual(payload["runtime"]["status"], "UNVERIFIED")
                self.assertEqual(payload["runtime"]["percent"], 0)


if __name__ == "__main__":
    unittest.main()
